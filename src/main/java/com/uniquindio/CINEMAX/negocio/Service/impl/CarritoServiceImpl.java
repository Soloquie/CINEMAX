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

@Service
@RequiredArgsConstructor
@Transactional
public class CarritoServiceImpl implements CarritoService {

    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final FuncionAsientoRepository funcionAsientoRepository;

    @Override
    public void addSeatHoldsToCart(String userEmail, List<Long> funcionAsientoIds, LocalDateTime expiraEn) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        CarritoEntity carrito = getOrCreateActiveCart(usuario);

        // Evitar duplicados: consultamos cuáles ya existen
        List<CarritoItemEntity> existentes = carritoItemRepository
                .findByCarritoIdAndTipoAndFuncionAsientoIdIn(
                        carrito.getId(), TipoCarritoItem.ASIENTO, funcionAsientoIds
                );

        var ya = existentes.stream().map(ci -> ci.getFuncionAsiento().getId()).collect(java.util.stream.Collectors.toSet());

        // Traer funcion_asientos (con funcion) para precio
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

        if (!nuevos.isEmpty()) {
            carritoItemRepository.saveAll(nuevos);
        }

        // Set expiración del carrito al máximo de la retención
        if (carrito.getExpiraEn() == null || (expiraEn != null && expiraEn.isAfter(carrito.getExpiraEn()))) {
            carrito.setExpiraEn(expiraEn);
        }
        carritoRepository.save(carrito);
    }

    @Override
    public void removeSeatHoldsFromCart(String userEmail, List<Long> funcionAsientoIds) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        CarritoEntity carrito = carritoRepository
                .findFirstByUsuarioIdAndEstadoOrderByActualizadoEnDesc(usuario.getId(), EstadoCarrito.ACTIVO)
                .orElse(null);

        if (carrito == null) return;

        carritoItemRepository.deleteByCarritoIdAndTipoAndFuncionAsientoIdIn(
                carrito.getId(), TipoCarritoItem.ASIENTO, funcionAsientoIds
        );

        // Recalcular expiraEn (simple): si no hay items, null
        List<CarritoItemEntity> items = carritoItemRepository.findItemsWithDetails(carrito.getId());
        if (items.isEmpty()) {
            carrito.setExpiraEn(null);
        } else {
            LocalDateTime max = null;
            for (CarritoItemEntity ci : items) {
                if (ci.getTipo() == TipoCarritoItem.ASIENTO && ci.getFuncionAsiento() != null) {
                    LocalDateTime e = ci.getFuncionAsiento().getRetencionExpira();
                    if (e != null && (max == null || e.isAfter(max))) max = e;
                }
            }
            carrito.setExpiraEn(max);
        }
        carritoRepository.save(carrito);
    }

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

        if (carrito.getId() == null) {
            return new CarritoResponseDTO(null, carrito.getEstado().name(), null, List.of());
        }

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
                        ci.getPrecioUnitario()
                );
            }
            // productos luego
            return new CarritoItemResponseDTO(ci.getId(), ci.getTipo().name(),
                    null, null, null, null, null, null, null, null,
                    ci.getPrecioUnitario());
        }).toList();

        return new CarritoResponseDTO(carrito.getId(), carrito.getEstado().name(), carrito.getExpiraEn(), dtoItems);
    }

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
}