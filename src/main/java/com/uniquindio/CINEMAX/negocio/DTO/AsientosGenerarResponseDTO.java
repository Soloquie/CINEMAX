package com.uniquindio.CINEMAX.negocio.DTO;

public record AsientosGenerarResponseDTO(
        Long salaId,
        int creados,
        int actualizados,
        int desactivados,
        long totalActivos
) {}