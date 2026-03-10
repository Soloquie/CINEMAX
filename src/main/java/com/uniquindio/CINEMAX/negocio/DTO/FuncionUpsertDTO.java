package com.uniquindio.CINEMAX.negocio.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * DTO (Data Transfer Object) para representar la información necesaria para crear o actualizar una función en el
 * sistema CINEMAX. Este DTO se utiliza para recibir los datos necesarios para realizar operaciones de inserción o
 * actualización de funciones, incluyendo detalles de la película, sala, horarios, idioma, subtítulos y precio base.
 */
public record FuncionUpsertDTO(
        @NotNull Long peliculaId,
        @NotNull Long salaId,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime inicio,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fin,

        String idioma,
        Boolean subtitulos,
        BigDecimal precioBase
) {}