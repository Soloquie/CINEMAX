package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.Entity.*;
import com.uniquindio.CINEMAX.Persistencia.Repository.*;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.ConfiteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfiteriaServiceImpl implements ConfiteriaService {

    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final InventarioMovimientoRepository inventarioMovimientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoPublicDTO> listarPublicos(String categoria) {
        List<ProductoEntity> productos;

        if (categoria == null || categoria.isBlank()) {
            productos = productoRepository.findByActivoTrueOrderByNombreAsc();
        } else {
            ProductoCategoria cat = ProductoCategoria.valueOf(categoria.trim().toUpperCase());
            productos = productoRepository.findByActivoTrueAndCategoriaOrderByNombreAsc(cat);
        }

        return productos.stream()
                .map(this::toPublicDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoAdminResponseDTO> listarAdmin() {
        return productoRepository.findAll().stream().map(this::toAdminDTO).toList();
    }

    @Override
    public ProductoAdminResponseDTO crear(ProductoUpsertDTO dto) {
        if (productoRepository.existsBySku(dto.sku().trim())) {
            throw new IllegalArgumentException("Ya existe un producto con ese SKU");
        }

        ProductoEntity producto = ProductoEntity.builder()
                .sku(dto.sku().trim())
                .nombre(dto.nombre().trim())
                .descripcion(dto.descripcion())
                .precio(dto.precio())
                .categoria(ProductoCategoria.valueOf(dto.categoria().trim().toUpperCase()))
                .imagenUrl(dto.imagenUrl())
                .activo(dto.activo() == null ? true : dto.activo())
                .build();

        ProductoEntity saved = productoRepository.save(producto);

        InventarioEntity inventario = InventarioEntity.builder()
                .producto(saved)
                .stock(dto.stock())
                .stockMinimo(dto.stockMinimo())
                .build();

        inventarioRepository.save(inventario);

        inventarioMovimientoRepository.save(
                InventarioMovimientoEntity.builder()
                        .producto(saved)
                        .tipo(TipoMovimientoInventario.ENTRADA)
                        .cantidad(dto.stock())
                        .motivo("Stock inicial")
                        .referencia("PRODUCTO-" + saved.getId())
                        .build()
        );

        return toAdminDTO(saved);
    }

    @Override
    public ProductoAdminResponseDTO actualizar(Long id, ProductoUpsertDTO dto) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));

        producto.setSku(dto.sku().trim());
        producto.setNombre(dto.nombre().trim());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecio(dto.precio());
        producto.setCategoria(ProductoCategoria.valueOf(dto.categoria().trim().toUpperCase()));
        producto.setImagenUrl(dto.imagenUrl());
        if (dto.activo() != null) producto.setActivo(dto.activo());

        productoRepository.save(producto);

        InventarioEntity inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Inventario no existe para el producto"));

        inventario.setStockMinimo(dto.stockMinimo());
        inventarioRepository.save(inventario);

        return toAdminDTO(producto);
    }

    @Override
    public void eliminar(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    public ProductoAdminResponseDTO actualizarStock(Long id, ActualizarStockDTO dto) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));

        InventarioEntity inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Inventario no existe"));

        TipoMovimientoInventario tipo = TipoMovimientoInventario.valueOf(dto.tipo().trim().toUpperCase());

        int stockActual = inventario.getStock();
        int nuevoStock = switch (tipo) {
            case ENTRADA -> stockActual + dto.cantidad();
            case SALIDA -> stockActual - dto.cantidad();
            case AJUSTE -> dto.cantidad();
        };

        if (nuevoStock < 0) {
            throw new IllegalStateException("No hay stock suficiente para realizar la operación");
        }

        inventario.setStock(nuevoStock);
        inventarioRepository.save(inventario);

        inventarioMovimientoRepository.save(
                InventarioMovimientoEntity.builder()
                        .producto(producto)
                        .tipo(tipo)
                        .cantidad(dto.cantidad())
                        .motivo(dto.motivo())
                        .referencia("STOCK-" + producto.getId())
                        .build()
        );

        return toAdminDTO(producto);
    }

    private ProductoPublicDTO toPublicDTO(ProductoEntity e) {
        InventarioEntity inv = inventarioRepository.findById(e.getId()).orElse(null);
        return new ProductoPublicDTO(
                e.getId(),
                e.getSku(),
                e.getNombre(),
                e.getDescripcion(),
                e.getPrecio(),
                e.getCategoria().name(),
                e.getImagenUrl(),
                e.getActivo(),
                inv != null ? inv.getStock() : 0
        );
    }

    private ProductoAdminResponseDTO toAdminDTO(ProductoEntity e) {
        InventarioEntity inv = inventarioRepository.findById(e.getId()).orElse(null);
        return new ProductoAdminResponseDTO(
                e.getId(),
                e.getSku(),
                e.getNombre(),
                e.getDescripcion(),
                e.getPrecio(),
                e.getCategoria().name(),
                e.getImagenUrl(),
                e.getActivo(),
                inv != null ? inv.getStock() : 0,
                inv != null ? inv.getStockMinimo() : 0
        );
    }
}