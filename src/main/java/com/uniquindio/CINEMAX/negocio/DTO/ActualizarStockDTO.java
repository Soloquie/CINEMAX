package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActualizarStockDTO(
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 0, message = "La cantidad no puede ser negativa")
        Integer cantidad,

        @NotBlank(message = "El tipo de movimiento es obligatorio")
        String tipo, // ENTRADA | SALIDA | AJUSTE

        String motivo
) {}