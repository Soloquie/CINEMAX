package com.uniquindio.CINEMAX.negocio.Model;

import java.time.LocalDate;
import java.util.Set;

public record Pelicula(
        Long id,
        String titulo,
        String sinopsis,
        Integer duracionMin,
        String clasificacion,
        LocalDate fechaEstreno,
        String posterUrl,
        Boolean activa,
        Set<Genero> generos
) {}