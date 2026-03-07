package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDate;
import java.util.Set;

public record PeliculaResponseDTO(
        Long id,
        String titulo,
        String sinopsis,
        Integer duracionMin,
        String clasificacion,
        LocalDate fechaEstreno,
        String posterUrl,
        Boolean activa,
        Set<GeneroDTO> generos
) {}