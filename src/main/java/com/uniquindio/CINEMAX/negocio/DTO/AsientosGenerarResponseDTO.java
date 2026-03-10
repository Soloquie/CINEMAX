package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) para representar la respuesta de la operación de generación de asientos en una sala de cine.
 * Este DTO se utiliza para enviar información sobre el resultado de la generación de asientos,
 * incluyendo el ID de la sala, la cantidad de asientos creados, actualizados, desactivados
 * y el total de asientos activos en la sala después de la operación.
 */
public record AsientosGenerarResponseDTO(
        Long salaId,
        int creados,
        int actualizados,
        int desactivados,
        long totalActivos
) {}