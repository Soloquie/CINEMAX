package com.uniquindio.CINEMAX.negocio.Model;

import java.time.LocalDate;
import java.util.Set;
/**
 * Modelo de dominio para representar una película en el sistema CINEMAX.
 * Este modelo se utiliza para almacenar y gestionar la información relacionada con las películas,
 * incluyendo su ID, título, sinopsis, duración, clasificación, fecha de estreno, URL del póster,
 * estado de actividad y los géneros asociados a la película.
 */
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