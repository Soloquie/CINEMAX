package com.uniquindio.CINEMAX.negocio.DTO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoSala;

public record SalaResponseDTO(
        Long id,
        Long cineId,
        String cineNombre,
        String nombre,
        TipoSala tipo,
        Boolean activa
) {}