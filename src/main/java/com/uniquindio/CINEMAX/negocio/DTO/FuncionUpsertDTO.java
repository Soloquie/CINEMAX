package com.uniquindio.CINEMAX.negocio.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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