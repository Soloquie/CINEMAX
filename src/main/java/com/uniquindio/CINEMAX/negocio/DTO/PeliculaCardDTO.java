package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) para representar la información básica de una película en formato de tarjeta
 * en el sistema CINEMAX. Este DTO se utiliza para enviar la información esencial de una película al cliente,
 * incluyendo su ID, título, URL del póster, duración en minutos, clasificación y fecha de estreno.
 */
import java.time.LocalDate;

public record PeliculaCardDTO(
        Long id,
        String titulo,
        String posterUrl,
        Integer duracionMin,
        String clasificacion,
        LocalDate fechaEstreno
) {}