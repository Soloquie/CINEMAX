package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.Entity.*;
import com.uniquindio.CINEMAX.Persistencia.Repository.*;
import com.uniquindio.CINEMAX.negocio.DTO.CarritoItemResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.CarritoResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Implementación de la interfaz CarritoService para gestionar el carrito de compras en el sistema CINEMAX.
 * Esta clase proporciona métodos para agregar y eliminar asientos retenidos en el carrito, así como para obtener el estado actual del carrito de un usuario.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CarritoServiceImpl implements CarritoService {
    // Repositorios necesarios para interactuar con la base de datos y gestionar el carrito,
    // los usuarios y los asientos de las funciones.
    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final FuncionAsientoRepository funcionAsientoRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    /**
     * Agrega asientos retenidos al carrito de un usuario específico. Si el usuario no tiene un carrito activo,
     * se crea uno nuevo.El método también establece la fecha de expiración del carrito según
     * la retención de los asientos.
     * @param userEmail El correo electrónico del usuario al que se le agregarán los asientos al carrito.
     * @param funcionAsientoIds Una lista de IDs de los asientos de función que se desean agregar al carrito.
     * @param expiraEn La fecha y hora en que expira la retención de los asientos, lo que también puede
     * establecer la expiración del carrito.
     */
    @Override
    public void addSeatHoldsToCart(String userEmail, List<Long> funcionAsientoIds, LocalDateTime expiraEn) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        CarritoEntity carrito = getOrCreateActiveCart(usuario);

        List<CarritoItemEntity> existentes = carritoItemRepository
                .findByCarritoIdAndTipoAndFuncionAsientoIdIn(
                        carrito.getId(), TipoCarritoItem.ASIENTO, funcionAsientoIds
                );

        var ya = existentes.stream().map(ci -> ci.getFuncionAsiento().getId()).collect(java.util.stream.Collectors.toSet());
        /** Se obtienen los detalles de los asientos de función que se desean agregar al carrito. Luego,
         *  se verifica cuáles de esos asientos ya están en el carrito para evitar duplicados.Para cada asiento de
         *  función que no esté ya en el carrito, se crea un nuevo CarritoItemEntity con el tipo ASIENTO,
         *  se establece su precio unitario según el precio base de la función y se agrega a una lista de nuevos items.
         *  Finalmente, si hay nuevos items para agregar, se guardan en la base de datos y se actualiza la fecha de expiración del carrito si es necesario.
         */
        List<FuncionAsientoEntity> fas = funcionAsientoRepository.findAllById(funcionAsientoIds);

        List<CarritoItemEntity> nuevos = new ArrayList<>();
        for (FuncionAsientoEntity fa : fas) {
            if (ya.contains(fa.getId())) continue;

            BigDecimal precio = fa.getFuncion().getPrecioBase(); // precio_base de la función
            if (precio == null) precio = BigDecimal.ZERO;

            CarritoItemEntity item = CarritoItemEntity.builder()
                    .carrito(carrito)
                    .tipo(TipoCarritoItem.ASIENTO)
                    .funcionAsiento(fa)
                    .producto(null)
                    .cantidad(1)
                    .precioUnitario(precio)
                    .build();

            nuevos.add(item);
        }
        // Guardar nuevos items en el carrito
        if (!nuevos.isEmpty()) {
            carritoItemRepository.saveAll(nuevos);
        }

        // Set expiración del carrito al máximo de la retención
        if (carrito.getExpiraEn() == null || (expiraEn != null && expiraEn.isAfter(carrito.getExpiraEn()))) {
            carrito.setExpiraEn(expiraEn);
        }
        carritoRepository.save(carrito);
    }

    /**
     * Elimina los asientos retenidos del carrito de un usuario específico. El método busca el carrito activo
     * del usuario y elimina los items correspondientes a los IDs de asientos de función proporcionados.
     * Después de eliminar los items, se recalcula la fecha de expiración del carrito en función de los items restantes.
     * Si no quedan items, la fecha de expiración se establece en null.
     * @param userEmail El correo electrónico del usuario al que se le eliminarán los asientos del carrito.
     * @param funcionAsientoIds Una lista de IDs de los asientos de función que se desean eliminar del carrito.
     */
    @Override
    public void removeSeatHoldsFromCart(String userEmail, List<Long> funcionAsientoIds) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        CarritoEntity carrito = carritoRepository
                .findFirstByUsuarioIdAndEstadoOrderByActualizadoEnDesc(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElse(null);

        if (carrito == null) return;

        // 1. Liberar asientos
        List<FuncionAsientoEntity> asientos = funcionAsientoRepository.findAllById(funcionAsientoIds);

        for (FuncionAsientoEntity fa : asientos) {
            fa.setEstado(EstadoFuncionAsiento.DISPONIBLE);
            fa.setRetencionExpira(null);
        }

        funcionAsientoRepository.saveAll(asientos);

        // 2. Eliminar boletas del carrito
        carritoItemRepository.deleteByCarritoIdAndTipoAndFuncionAsientoIdIn(
                carrito.getId(), TipoCarritoItem.ASIENTO, funcionAsientoIds
        );

        // 3. Verificar si quedaron asientos
        List<CarritoItemEntity> itemsRestantes = carritoItemRepository.findItemsWithDetails(carrito.getId());

        boolean tieneAsientos = itemsRestantes.stream()
                .anyMatch(ci -> ci.getTipo() == TipoCarritoItem.ASIENTO);

        // 4. Si ya no quedan asientos, eliminar también la confitería
        if (!tieneAsientos) {
            carritoItemRepository.deleteByCarritoIdAndTipo(
                    carrito.getId(), TipoCarritoItem.PRODUCTO
            );

            // volver a cargar items porque ya cambió el carrito
            itemsRestantes = carritoItemRepository.findItemsWithDetails(carrito.getId());
        }

        // 5. Recalcular expiración
        if (itemsRestantes.isEmpty()) {
            carrito.setExpiraEn(null);
        } else {
            LocalDateTime max = null;

            for (CarritoItemEntity ci : itemsRestantes) {
                if (ci.getTipo() == TipoCarritoItem.ASIENTO && ci.getFuncionAsiento() != null) {
                    LocalDateTime e = ci.getFuncionAsiento().getRetencionExpira();
                    if (e != null && (max == null || e.isAfter(max))) {
                        max = e;
                    }
                }
            }

            carrito.setExpiraEn(max);
        }

        carritoRepository.save(carrito);
    }

    /**
     * Obtiene el estado actual del carrito de un usuario específico. El método busca el carrito activo del usuario
     * y devuelve su información, incluyendo los items que contiene. Si el usuario no tiene un carrito activo, se devuelve un carrito vacío con estado ACTIVO.
     * @param userEmail El correo electrónico del usuario cuyo carrito se desea obtener.
     * @return Un objeto CarritoResponseDTO que contiene la información del carrito, incluyendo su ID, estado,
     * fecha de expiración y una lista de items en el carrito.
     */
    @Override
    @Transactional(readOnly = true)
    public CarritoResponseDTO getMyCart(String userEmail) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        CarritoEntity carrito = carritoRepository
                .findFirstByUsuarioIdAndEstadoOrderByActualizadoEnDesc(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElseGet(() -> CarritoEntity.builder()
                        .usuario(usuario)
                        .estado(EstadoCarrito.ACTIVO)
                        .expiraEn(null)
                        .build());
        // Si el carrito no tiene ID, significa que es un carrito nuevo y vacío, por lo
        // que se devuelve un DTO con estado ACTIVO y sin items.
        if (carrito.getId() == null) {
            return new CarritoResponseDTO(null, carrito.getEstado().name(), null, List.of());
        }
        purgeExpiredSeatItems(carrito);
        List<CarritoItemEntity> items = carritoItemRepository.findItemsWithDetails(carrito.getId());

        List<CarritoItemResponseDTO> dtoItems = items.stream().map(ci -> {
            if (ci.getTipo() == TipoCarritoItem.ASIENTO && ci.getFuncionAsiento() != null) {
                var fa = ci.getFuncionAsiento();
                var a = fa.getAsiento();
                var f = fa.getFuncion();
                var p = f.getPelicula();
                var s = f.getSala();
                var c = s.getCine();

                return new CarritoItemResponseDTO(
                        ci.getId(),
                        ci.getTipo().name(),
                        fa.getId(),
                        f.getId(),
                        p.getTitulo(),
                        s.getNombre(),
                        c.getNombre(),
                        f.getInicio(),
                        a.getFila(),
                        a.getNumero(),
                        null,
                        null,
                        null,
                        null,
                        1,
                        ci.getPrecioUnitario(),
                        ci.getPrecioUnitario()
                );
            } else if (ci.getTipo() == TipoCarritoItem.PRODUCTO && ci.getProducto() != null) {
                var pr = ci.getProducto();

                return new CarritoItemResponseDTO(
                        ci.getId(),
                        ci.getTipo().name(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        pr.getId(),
                        pr.getNombre(),
                        pr.getDescripcion(),
                        pr.getImagenUrl(),
                        ci.getCantidad(),
                        ci.getPrecioUnitario(),
                        ci.getPrecioUnitario().multiply(java.math.BigDecimal.valueOf(ci.getCantidad()))
                );
            }

            throw new IllegalStateException("Item de carrito inválido o incompleto: " + ci.getId());
        }).toList();

        return new CarritoResponseDTO(carrito.getId(), carrito.getEstado().name(), carrito.getExpiraEn(), dtoItems);
    }
    // Método privado para obtener el carrito activo de un usuario o crear uno nuevo si no existe.
    // Este método se utiliza internamente para asegurar que siempre haya un carrito activo disponible para el usuario.
    private CarritoEntity getOrCreateActiveCart(UsuarioEntity usuario) {
        return carritoRepository
                .findFirstByUsuarioIdAndEstadoOrderByActualizadoEnDesc(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElseGet(() -> carritoRepository.save(
                        CarritoEntity.builder()
                                .usuario(usuario)
                                .estado(EstadoCarrito.ACTIVO)
                                .expiraEn(null)
                                .build()
                ));
    }

    @Override
    public void addProductsToCart(String userEmail, Long productoId, int cantidad) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        CarritoEntity carrito = getOrCreateActiveCart(usuario);

        boolean tieneAsientos = carritoItemRepository.findItemsWithDetails(carrito.getId()).stream()
                .anyMatch(ci -> ci.getTipo() == TipoCarritoItem.ASIENTO);

        if (!tieneAsientos) {
            throw new IllegalStateException("Debes seleccionar al menos una función/asiento antes de agregar confitería");
        }

        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));

        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new IllegalStateException("El producto no está disponible");
        }

        InventarioEntity inventario = inventarioRepository.findById(productoId)
                .orElseThrow(() -> new IllegalStateException("Inventario no existe"));

        if (inventario.getStock() < cantidad) {
            throw new IllegalStateException("No hay stock suficiente");
        }

        CarritoItemEntity item = carritoItemRepository
                .findByCarritoIdAndTipoAndProductoId(carrito.getId(), TipoCarritoItem.PRODUCTO, productoId)
                .orElse(null);

        if (item == null) {
            item = CarritoItemEntity.builder()
                    .carrito(carrito)
                    .tipo(TipoCarritoItem.PRODUCTO)
                    .producto(producto)
                    .cantidad(cantidad)
                    .precioUnitario(producto.getPrecio())
                    .build();
        } else {
            int nuevaCantidad = item.getCantidad() + cantidad;
            if (inventario.getStock() < nuevaCantidad) {
                throw new IllegalStateException("No hay stock suficiente para aumentar la cantidad");
            }
            item.setCantidad(nuevaCantidad);
        }

        carritoItemRepository.save(item);
    }

    @Override
    public void updateProductQuantity(String userEmail, Long productoId, int cantidad) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        CarritoEntity carrito = carritoRepository
                .findFirstByUsuarioIdAndEstadoOrderByActualizadoEnDesc(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new IllegalArgumentException("No existe carrito activo"));

        CarritoItemEntity item = carritoItemRepository
                .findByCarritoIdAndTipoAndProductoId(carrito.getId(), TipoCarritoItem.PRODUCTO, productoId)
                .orElseThrow(() -> new IllegalArgumentException("El producto no está en el carrito"));

        InventarioEntity inventario = inventarioRepository.findById(productoId)
                .orElseThrow(() -> new IllegalStateException("Inventario no existe"));

        if (inventario.getStock() < cantidad) {
            throw new IllegalStateException("No hay stock suficiente");
        }

        item.setCantidad(cantidad);
        carritoItemRepository.save(item);
    }

    @Override
    public void removeProductFromCart(String userEmail, Long productoId) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        CarritoEntity carrito = carritoRepository
                .findFirstByUsuarioIdAndEstadoOrderByActualizadoEnDesc(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElse(null);

        if (carrito == null) return;

        carritoItemRepository.deleteByCarritoIdAndTipoAndProductoId(
                carrito.getId(), TipoCarritoItem.PRODUCTO, productoId
        );
    }

    private void purgeExpiredSeatItems(CarritoEntity carrito) {
        List<CarritoItemEntity> items = carritoItemRepository.findItemsWithDetails(carrito.getId());
        LocalDateTime now = LocalDateTime.now();

        List<Long> expirados = items.stream()
                .filter(ci -> ci.getTipo() == TipoCarritoItem.ASIENTO)
                .filter(ci -> ci.getFuncionAsiento() != null)
                .filter(ci -> ci.getFuncionAsiento().getRetencionExpira() != null)
                .filter(ci -> !ci.getFuncionAsiento().getRetencionExpira().isAfter(now))
                .map(ci -> ci.getFuncionAsiento().getId())
                .toList();

        if (expirados.isEmpty()) return;

        removeSeatHoldsFromCart(carrito.getUsuario().getEmail(), expirados);
    }
}