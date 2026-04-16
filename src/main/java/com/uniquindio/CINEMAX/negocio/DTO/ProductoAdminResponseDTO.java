package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;

public record ProductoAdminResponseDTO(
        Long id,
        String sku,
        String nombre,
        String descripcion,
        BigDecimal precio,
        String categoria,
        String imagenUrl,
        Boolean activo,
        Integer stock,
        Integer stockMinimo
) {}