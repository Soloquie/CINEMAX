package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.*;

import java.util.List;

public interface ConfiteriaService {
    List<ProductoPublicDTO> listarPublicos(String categoria);
    List<ProductoAdminResponseDTO> listarAdmin();
    ProductoAdminResponseDTO crear(ProductoUpsertFormDTO form);
    ProductoAdminResponseDTO actualizar(Long id, ProductoUpsertFormDTO form);
    void eliminar(Long id);
    ProductoAdminResponseDTO actualizarStock(Long id, ActualizarStockDTO dto);
}