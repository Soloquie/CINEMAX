package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.Entity.*;
import com.uniquindio.CINEMAX.Persistencia.Repository.*;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Exception.CantidadInvalidaException;
import com.uniquindio.CINEMAX.negocio.Exception.FiltroInvalidoException;
import com.uniquindio.CINEMAX.negocio.Exception.InventarioNoEncontradoException;
import com.uniquindio.CINEMAX.negocio.Exception.ProductoNoEncontradoException;
import com.uniquindio.CINEMAX.negocio.Exception.SkuDuplicadoException;
import com.uniquindio.CINEMAX.negocio.Exception.StockInsuficienteException;
import com.uniquindio.CINEMAX.negocio.Service.CloudinaryService;
import com.uniquindio.CINEMAX.negocio.Service.ConfiteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfiteriaServiceImpl implements ConfiteriaService {

    private final CloudinaryService cloudinaryService;
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
            try {
                ProductoCategoria cat = ProductoCategoria.valueOf(categoria.trim().toUpperCase());
                productos = productoRepository.findByActivoTrueAndCategoriaOrderByNombreAsc(cat);
            } catch (IllegalArgumentException ex) {
                throw new FiltroInvalidoException("La categoría proporcionada no es válida.");
            }
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
    public ProductoAdminResponseDTO crear(ProductoUpsertFormDTO form) {
        if (productoRepository.existsBySku(form.getSku().trim())) {
            throw new SkuDuplicadoException();
        }

        if (form.getStock() < 0) {
            throw new CantidadInvalidaException("El stock inicial no puede ser negativo.");
        }

        if (form.getStockMinimo() < 0) {
            throw new CantidadInvalidaException("El stock mínimo no puede ser negativo.");
        }

        String imageUrl = cloudinaryService.uploadProductImage(form.getImagen());

        ProductoEntity producto = ProductoEntity.builder()
                .sku(form.getSku().trim())
                .nombre(form.getNombre().trim())
                .descripcion(form.getDescripcion())
                .precio(form.getPrecio())
                .categoria(ProductoCategoria.valueOf(form.getCategoria().trim().toUpperCase()))
                .imagenUrl(imageUrl)
                .activo(form.getActivo() == null ? true : form.getActivo())
                .build();

        ProductoEntity saved = productoRepository.save(producto);

        InventarioEntity inventario = InventarioEntity.builder()
                .producto(saved)
                .stock(form.getStock())
                .stockMinimo(form.getStockMinimo())
                .build();

        inventarioRepository.save(inventario);

        inventarioMovimientoRepository.save(
                InventarioMovimientoEntity.builder()
                        .producto(saved)
                        .tipo(TipoMovimientoInventario.ENTRADA)
                        .cantidad(form.getStock())
                        .motivo("Stock inicial")
                        .referencia("PRODUCTO-" + saved.getId())
                        .build()
        );

        return toAdminDTO(saved);
    }

    @Override
    public ProductoAdminResponseDTO actualizar(Long id, ProductoUpsertFormDTO form) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);

        if (form.getStockMinimo() < 0) {
            throw new CantidadInvalidaException("El stock mínimo no puede ser negativo.");
        }

        producto.setSku(form.getSku().trim());
        producto.setNombre(form.getNombre().trim());
        producto.setDescripcion(form.getDescripcion());
        producto.setPrecio(form.getPrecio());
        producto.setCategoria(ProductoCategoria.valueOf(form.getCategoria().trim().toUpperCase()));
        if (form.getActivo() != null) {
            producto.setActivo(form.getActivo());
        }

        String nuevaImagenUrl = cloudinaryService.uploadProductImage(form.getImagen());
        if (nuevaImagenUrl != null) {
            producto.setImagenUrl(nuevaImagenUrl);
        }

        productoRepository.save(producto);

        InventarioEntity inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario no existe para el producto."));

        inventario.setStockMinimo(form.getStockMinimo());
        inventarioRepository.save(inventario);

        return toAdminDTO(producto);
    }

    @Override
    public void eliminar(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    public ProductoAdminResponseDTO actualizarStock(Long id, ActualizarStockDTO dto) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);

        InventarioEntity inventario = inventarioRepository.findById(id)
                .orElseThrow(InventarioNoEncontradoException::new);

        if (dto.cantidad() < 0) {
            throw new CantidadInvalidaException("La cantidad no puede ser negativa.");
        }

        TipoMovimientoInventario tipo;
        try {
            tipo = TipoMovimientoInventario.valueOf(dto.tipo().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new FiltroInvalidoException("El tipo de movimiento no es válido.");
        }

        int stockActual = inventario.getStock();
        int nuevoStock = switch (tipo) {
            case ENTRADA -> stockActual + dto.cantidad();
            case SALIDA -> stockActual - dto.cantidad();
            case AJUSTE -> dto.cantidad();
        };

        if (nuevoStock < 0) {
            throw new StockInsuficienteException("No hay stock suficiente para realizar la operación.");
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