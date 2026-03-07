package com.uniquindio.CINEMAX.negocio.DTO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoAsiento;

public record AsientoResponseDTO(
        Long id,
        String fila,
        Integer numero,
        TipoAsiento tipo,
        Boolean activo
) {}