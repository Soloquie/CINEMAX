package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.Entity.*;
import com.uniquindio.CINEMAX.Persistencia.Repository.*;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Exception.*;
import com.uniquindio.CINEMAX.negocio.Service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Implementación de la interfaz VentaService para gestionar la confirmación atómica de ventas en CINEMAX.
 * Esta clase es el punto crítico del cierre de compra, ya que revalida asientos, registra boletas,
 * descuenta inventario, confirma el pago y vacía el carrito dentro de una sola transacción.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VentaServiceImpl implements VentaService {

    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final BoletaRepository boletaRepository;
    private final VentaProductoRepository ventaProductoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final CarritoRepository carritoRepository;
    private final FuncionAsientoRepository funcionAsientoRepository;
    private final InventarioRepository inventarioRepository;
    private final InventarioMovimientoRepository inventarioMovimientoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Confirma una venta desde un pago aprobado. El método es idempotente:
     * si ya existe una venta asociada al pago, devuelve esa venta sin duplicarla.
     */
    @Override
    public VentaResponseDTO confirmarVentaDesdePago(String referencia) {
        PagoEntity pago = pagoRepository.findByReferenciaForUpdate(referencia)
                .orElseThrow(PagoNoEncontradoException::new);

        Optional<VentaEntity> ventaExistente = ventaRepository.findByPagoId(pago.getId());
        if (ventaExistente.isPresent()) {
            return toResponseDTO(ventaExistente.get());
        }

        if (pago.getEstado() != EstadoPago.APROBADO && pago.getEstado() != EstadoPago.CONFIRMADO) {
            throw new PagoInvalidoException("El pago no está aprobado para confirmar la venta.");
        }

        CarritoEntity carrito = pago.getCarrito();
        List<CarritoItemEntity> items = carritoItemRepository.findItemsWithDetails(carrito.getId());

        if (items.isEmpty()) {
            throw new CarritoInvalidoException("El carrito no tiene ítems para confirmar la venta.");
        }

        List<CarritoItemEntity> seatItems = items.stream()
                .filter(i -> i.getTipo() == TipoCarritoItem.ASIENTO)
                .toList();

        if (seatItems.isEmpty()) {
            throw new ConfiteriaNoPermitidaSinBoletaException("No se puede confirmar una venta sin boletas.");
        }

        List<Long> funcionAsientoIds = seatItems.stream()
                .map(i -> i.getFuncionAsiento().getId())
                .toList();

        List<FuncionAsientoEntity> lockedSeats = funcionAsientoRepository.findAllByIdInForUpdate(funcionAsientoIds);

        if (lockedSeats.size() != funcionAsientoIds.size()) {
            throw new FuncionAsientoNoEncontradoException("Uno o más asientos de función no existen.");
        }

        Map<Long, FuncionAsientoEntity> lockedById = lockedSeats.stream()
                .collect(Collectors.toMap(FuncionAsientoEntity::getId, fa -> fa));

        validarAsientosAntesDeVender(seatItems, lockedById);

        BigDecimal totalCalculado = calcularTotal(items);

        if (pago.getMonto().compareTo(totalCalculado) != 0) {
            throw new PagoInvalidoException("El monto del pago no coincide con el total actual del carrito.");
        }

        VentaEntity venta = ventaRepository.save(VentaEntity.builder()
                .codigo(generarCodigoVenta())
                .usuario(pago.getUsuario())
                .pago(pago)
                .total(totalCalculado)
                .estado(EstadoVenta.CONFIRMADA)
                .build());

        List<BoletaEntity> boletas = crearBoletas(venta, seatItems, lockedById);
        boletaRepository.saveAll(boletas);

        List<VentaProductoEntity> productos = crearProductosVenta(venta, items);
        ventaProductoRepository.saveAll(productos);

        for (FuncionAsientoEntity fa : lockedSeats) {
            fa.setEstado(EstadoFuncionAsiento.VENDIDO);
            fa.setRetencionExpira(null);
        }
        funcionAsientoRepository.saveAll(lockedSeats);

        descontarInventarioProductos(items, venta);

        carritoItemRepository.deleteAll(items);
        carrito.setExpiraEn(null);
        carritoRepository.save(carrito);

        pago.setEstado(EstadoPago.CONFIRMADO);
        pago.setConfirmadoEn(LocalDateTime.now());
        pago.setMensajeError(null);
        pagoRepository.save(pago);

        return toResponseDTO(venta);
    }

    /**
     * Consulta el detalle de una venta validando que pertenezca al usuario autenticado.
     */
    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO detalleVenta(Long ventaId, String userEmail) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(UsuarioNoEncontradoException::new);

        VentaEntity venta = ventaRepository.findById(ventaId)
                .orElseThrow(VentaNoEncontradaException::new);

        if (!venta.getUsuario().getId().equals(usuario.getId())) {
            throw new VentaNoEncontradaException("No existe una venta asociada al usuario autenticado.");
        }

        return toResponseDTO(venta);
    }

    private void validarAsientosAntesDeVender(List<CarritoItemEntity> seatItems, Map<Long, FuncionAsientoEntity> lockedById) {
        LocalDateTime now = LocalDateTime.now();

        for (CarritoItemEntity item : seatItems) {
            FuncionAsientoEntity fa = lockedById.get(item.getFuncionAsiento().getId());

            if (fa == null) {
                throw new FuncionAsientoNoEncontradoException();
            }

            if (boletaRepository.existsByFuncionAsientoId(fa.getId())) {
                throw new AsientoNoDisponibleException("El asiento " + fa.getAsiento().getFila() + fa.getAsiento().getNumero() + " ya fue vendido.");
            }

            if (fa.getEstado() == EstadoFuncionAsiento.VENDIDO) {
                throw new AsientoNoDisponibleException("El asiento " + fa.getAsiento().getFila() + fa.getAsiento().getNumero() + " ya fue vendido.");
            }

            if (fa.getRetencionExpira() == null || !fa.getRetencionExpira().isAfter(now)) {
                throw new ReservaExpiradaException("La reserva del asiento " + fa.getAsiento().getFila() + fa.getAsiento().getNumero() + " expiró.");
            }
        }
    }

    private List<BoletaEntity> crearBoletas(
            VentaEntity venta,
            List<CarritoItemEntity> seatItems,
            Map<Long, FuncionAsientoEntity> lockedById
    ) {
        List<BoletaEntity> boletas = new ArrayList<>();

        for (CarritoItemEntity item : seatItems) {
            FuncionAsientoEntity fa = lockedById.get(item.getFuncionAsiento().getId());

            var asiento = fa.getAsiento();
            var funcion = fa.getFuncion();
            var pelicula = funcion.getPelicula();
            var sala = funcion.getSala();
            var cine = sala.getCine();

            boletas.add(BoletaEntity.builder()
                    .codigo(generarCodigoBoleta())
                    .venta(venta)
                    .funcionAsiento(fa)
                    .peliculaTitulo(pelicula.getTitulo())
                    .cineNombre(cine.getNombre())
                    .salaNombre(sala.getNombre())
                    .fila(asiento.getFila())
                    .numero(asiento.getNumero())
                    .inicioFuncion(funcion.getInicio())
                    .precio(item.getPrecioUnitario())
                    .build());
        }

        return boletas;
    }

    private List<VentaProductoEntity> crearProductosVenta(VentaEntity venta, List<CarritoItemEntity> items) {
        return items.stream()
                .filter(i -> i.getTipo() == TipoCarritoItem.PRODUCTO && i.getProducto() != null)
                .map(i -> {
                    int cantidad = i.getCantidad() == null ? 1 : i.getCantidad();
                    BigDecimal subtotal = i.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad));

                    return VentaProductoEntity.builder()
                            .venta(venta)
                            .producto(i.getProducto())
                            .nombreProducto(i.getProducto().getNombre())
                            .cantidad(cantidad)
                            .precioUnitario(i.getPrecioUnitario())
                            .subtotal(subtotal)
                            .build();
                })
                .toList();
    }

    private void descontarInventarioProductos(List<CarritoItemEntity> items, VentaEntity venta) {
        List<CarritoItemEntity> productos = items.stream()
                .filter(i -> i.getTipo() == TipoCarritoItem.PRODUCTO && i.getProducto() != null)
                .toList();

        for (CarritoItemEntity item : productos) {
            Long productoId = item.getProducto().getId();
            int cantidad = item.getCantidad() == null ? 1 : item.getCantidad();

            InventarioEntity inventario = inventarioRepository.findById(productoId)
                    .orElseThrow(InventarioNoEncontradoException::new);

            if (inventario.getStock() < cantidad) {
                throw new StockInsuficienteException("No hay stock suficiente para el producto " + item.getProducto().getNombre() + ".");
            }

            inventario.setStock(inventario.getStock() - cantidad);
            inventarioRepository.save(inventario);

            inventarioMovimientoRepository.save(
                    InventarioMovimientoEntity.builder()
                            .producto(item.getProducto())
                            .tipo(TipoMovimientoInventario.SALIDA)
                            .cantidad(cantidad)
                            .motivo("Venta confirmada")
                            .referencia("VENTA-" + venta.getCodigo())
                            .build()
            );
        }
    }

    private BigDecimal calcularTotal(List<CarritoItemEntity> items) {
        return items.stream()
                .map(item -> {
                    int cantidad = item.getCantidad() == null ? 1 : item.getCantidad();
                    return item.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private VentaResponseDTO toResponseDTO(VentaEntity venta) {
        List<BoletaResponseDTO> boletas = boletaRepository.findByVentaId(venta.getId())
                .stream()
                .map(b -> new BoletaResponseDTO(
                        b.getId(),
                        b.getCodigo(),
                        b.getPeliculaTitulo(),
                        b.getCineNombre(),
                        b.getSalaNombre(),
                        b.getFila(),
                        b.getNumero(),
                        b.getInicioFuncion(),
                        b.getPrecio()
                ))
                .toList();

        List<VentaProductoResponseDTO> productos = ventaProductoRepository.findByVentaId(venta.getId())
                .stream()
                .map(p -> new VentaProductoResponseDTO(
                        p.getProducto().getId(),
                        p.getNombreProducto(),
                        p.getCantidad(),
                        p.getPrecioUnitario(),
                        p.getSubtotal()
                ))
                .toList();

        return new VentaResponseDTO(
                venta.getId(),
                venta.getCodigo(),
                venta.getEstado().name(),
                venta.getTotal(),
                boletas,
                productos
        );
    }

    private String generarCodigoVenta() {
        return "VEN-CINEMAX-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String generarCodigoBoleta() {
        return "BOL-CINEMAX-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraResumenDTO> listarMisCompras(String userEmail) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(UsuarioNoEncontradoException::new);

        return ventaRepository.findMisComprasByUsuarioEmail(usuario.getEmail())
                .stream()
                .map(v -> new CompraResumenDTO(
                        v.getId(),
                        v.getCodigo(),
                        v.getEstado().name(),
                        v.getTotal(),
                        v.getPago() != null ? v.getPago().getReferencia() : null,
                        v.getPago() != null ? v.getPago().getEstado().name() : null
                ))
                .toList();
    }
}