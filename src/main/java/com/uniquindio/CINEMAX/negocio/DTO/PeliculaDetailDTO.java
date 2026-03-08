package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDate;
import java.util.Set;

public record PeliculaDetailDTO(
        Long id,
        String titulo,
        String sinopsis,
        Integer duracionMin,
        String clasificacion,
        LocalDate fechaEstreno,
        String posterUrl,
        boolean activa,
        Set<String> generos
) {}