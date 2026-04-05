package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
/**
 * DTO (Data Transfer Object) para representar la solicitud de liberación de asientos en el sistema CINEMAX.
 * Este DTO se utiliza para recibir una lista de IDs de asientos que se desean liberar para una función específica,
 * con validaciones para asegurar que la lista no esté vacía.
 */
public record ReleaseAsientosRequestDTO(
        @NotEmpty List<Long> funcionAsientoIds
) {}