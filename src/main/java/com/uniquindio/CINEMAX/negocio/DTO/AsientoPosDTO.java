package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AsientoPosDTO(
        @NotBlank String fila,
        @Min(1) int numero
) {}