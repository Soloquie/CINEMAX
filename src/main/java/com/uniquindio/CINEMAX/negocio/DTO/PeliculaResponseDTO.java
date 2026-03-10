package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDate;
import java.util.Set;
/**
 * DTO (Data Transfer Object) para representar la información detallada de una película en el sistema CINEMAX.
 * Este DTO se utiliza para enviar información completa sobre una película al cliente, incluyendo su ID, título,
 * sinopsis, duración en minutos, clasificación, fecha de estreno, URL del póster, estado de actividad
 * y los géneros asociados.
 */
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