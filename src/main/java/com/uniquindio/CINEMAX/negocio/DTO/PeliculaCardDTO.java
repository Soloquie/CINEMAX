package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDate;

public record PeliculaCardDTO(
        Long id,
        String titulo,
        String posterUrl,
        Integer duracionMin,
        String clasificacion,
        LocalDate fechaEstreno
) {}