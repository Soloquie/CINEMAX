package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
/**
 * DTO (Data Transfer Object) para representar la posición de un asiento en una función de cine.
 * Este DTO se utiliza para recibir la información de la fila y el número del asiento que se desea seleccionar,
 * reservar o liberar. Contiene validaciones para asegurar que la fila no esté vacía
 * y que el número del asiento sea mayor o igual a 1.
 */
public record AsientoPosDTO(
        @NotBlank String fila,
        @Min(1) int numero
) {}