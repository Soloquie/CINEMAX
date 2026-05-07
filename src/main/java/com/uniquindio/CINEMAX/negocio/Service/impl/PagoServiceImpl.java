package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.Entity.*;
import com.uniquindio.CINEMAX.Persistencia.Repository.*;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Exception.CarritoInvalidoException;
import com.uniquindio.CINEMAX.negocio.Exception.ConfiteriaNoPermitidaSinBoletaException;
import com.uniquindio.CINEMAX.negocio.Exception.PagoNoEncontradoException;
import com.uniquindio.CINEMAX.negocio.Exception.UsuarioNoEncontradoException;
import com.uniquindio.CINEMAX.negocio.Service.PagoService;
import com.uniquindio.CINEMAX.negocio.Service.VentaService;
import com.uniquindio.CINEMAX.negocio.Service.WompiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
/**
 * Implementación de la interfaz PagoService para gestionar el inicio y seguimiento de pagos en CINEMAX.
 * Esta clase crea pagos pendientes desde el carrito del usuario, genera los datos del checkout y procesa eventos webhook.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final PagoRepository pagoRepository;
    private final PagoEventoRepository pagoEventoRepository;
    private final VentaRepository ventaRepository;
    private final WompiService wompiService;
    private final VentaService ventaService;

    /**
     * Crea un checkout de pago a partir del carrito activo del usuario autenticado.
     * El total se calcula en backend para evitar manipulación desde el frontend.
     */
    @Override
    public CheckoutPagoResponseDTO crearCheckout(String userEmail) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(UsuarioNoEncontradoException::new);

        CarritoEntity carrito = carritoRepository
                .findFirstByUsuarioIdAndEstadoOrderByActualizadoEnDesc(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new CarritoInvalidoException("No existe un carrito activo para iniciar el pago."));

        List<CarritoItemEntity> items = carritoItemRepository.findItemsWithDetails(carrito.getId());

        if (items.isEmpty()) {
            throw new CarritoInvalidoException("El carrito está vacío.");
        }

        boolean tieneAsientos = items.stream().anyMatch(i -> i.getTipo() == TipoCarritoItem.ASIENTO);
        if (!tieneAsientos) {
            throw new ConfiteriaNoPermitidaSinBoletaException("Debes tener al menos una boleta en el carrito antes de pagar.");
        }

        validarHoldsVigentes(items);

        BigDecimal total = calcularTotal(items);
        Long montoCentavos = total.multiply(BigDecimal.valueOf(100)).longValueExact();

        PagoEntity pago = PagoEntity.builder()
                .usuario(usuario)
                .carrito(carrito)
                .referencia(generarReferencia(usuario.getId()))
                .proveedor(ProveedorPago.WOMPI)
                .monto(total)
                .montoCentavos(montoCentavos)
                .moneda("COP")
                .estado(EstadoPago.PENDIENTE)
                .build();

        PagoEntity saved = pagoRepository.save(pago);
        WompiCheckoutDataDTO checkout = wompiService.construirCheckout(saved);

        return new CheckoutPagoResponseDTO(
                saved.getReferencia(),
                saved.getMontoCentavos(),
                saved.getMoneda(),
                checkout.publicKey(),
                checkout.integritySignature(),
                checkout.redirectUrl(),
                checkout.environment(),
                usuario.getEmail()
        );
    }

    /**
     * Consulta el estado de un pago y su venta asociada para el usuario autenticado.
     */
    @Override
    @Transactional(readOnly = true)
    public PagoEstadoResponseDTO consultarEstado(String referencia, String userEmail) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(UsuarioNoEncontradoException::new);

        PagoEntity pago = pagoRepository.findByReferencia(referencia)
                .orElseThrow(PagoNoEncontradoException::new);

        if (!pago.getUsuario().getId().equals(usuario.getId())) {
            throw new PagoNoEncontradoException("No existe un pago asociado al usuario autenticado.");
        }

        var ventaOpt = ventaRepository.findByPagoId(pago.getId());

        return new PagoEstadoResponseDTO(
                pago.getReferencia(),
                pago.getEstado().name(),
                ventaOpt.map(v -> v.getEstado().name()).orElse(null),
                ventaOpt.map(VentaEntity::getId).orElse(null),
                ventaOpt.map(VentaEntity::getCodigo).orElse(null),
                pago.getMensajeError()
        );
    }

    /**
     * Procesa un evento webhook recibido desde Wompi.
     * Si el evento corresponde a una transacción aprobada, dispara la confirmación transaccional de la venta.
     */
    @Override
    public void procesarWebhookWompi(WompiWebhookDTO webhook) {
        String referencia = wompiService.extraerReferencia(webhook);
        String transactionId = wompiService.extraerTransactionId(webhook);
        String estadoTransaccion = wompiService.extraerEstadoTransaccion(webhook);

        PagoEntity pago = null;

        if (referencia != null) {
            pago = pagoRepository.findByReferencia(referencia).orElse(null);
        }

        pagoEventoRepository.save(PagoEventoEntity.builder()
                .pago(pago)
                .proveedor(ProveedorPago.WOMPI.name())
                .eventType(webhook == null ? null : webhook.event())
                .transactionId(transactionId)
                .estadoTransaccion(estadoTransaccion)
                .payloadJson(wompiService.payloadToJson(webhook))
                .build());

        if (pago == null) {
            return;
        }

        if (transactionId != null && pago.getWompiTransactionId() == null) {
            pago.setWompiTransactionId(transactionId);
        }

        String normalizedStatus = estadoTransaccion == null ? "" : estadoTransaccion.trim().toUpperCase();

        if ("APPROVED".equals(normalizedStatus)) {
            if (pago.getEstado() == EstadoPago.CONFIRMADO) {
                pagoRepository.save(pago);
                return;
            }

            pago.setEstado(EstadoPago.APROBADO);
            pagoRepository.save(pago);

            try {
                ventaService.confirmarVentaDesdePago(pago.getReferencia());
            } catch (RuntimeException ex) {
                pago.setEstado(EstadoPago.FALLIDO_CONFIRMACION);
                pago.setMensajeError(ex.getMessage());
                pagoRepository.save(pago);
                throw ex;
            }

        } else if ("DECLINED".equals(normalizedStatus) || "VOIDED".equals(normalizedStatus) || "ERROR".equals(normalizedStatus)) {
            pago.setEstado(EstadoPago.RECHAZADO);
            pago.setMensajeError("Pago rechazado por el proveedor.");
            pagoRepository.save(pago);
        } else {
            pago.setEstado(EstadoPago.PENDIENTE);
            pagoRepository.save(pago);
        }
    }

    private void validarHoldsVigentes(List<CarritoItemEntity> items) {
        LocalDateTime now = LocalDateTime.now();

        for (CarritoItemEntity item : items) {
            if (item.getTipo() == TipoCarritoItem.ASIENTO && item.getFuncionAsiento() != null) {
                LocalDateTime expira = item.getFuncionAsiento().getRetencionExpira();

                if (expira == null || !expira.isAfter(now)) {
                    throw new CarritoInvalidoException("La reserva de uno o más asientos ha expirado.");
                }

                if (item.getFuncionAsiento().getEstado() == EstadoFuncionAsiento.VENDIDO) {
                    throw new CarritoInvalidoException("Uno o más asientos ya fueron vendidos.");
                }
            }
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

    private String generarReferencia(Long usuarioId) {
        return "CINEMAX-" + usuarioId + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}