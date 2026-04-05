package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDateTime;
import java.util.List;
/**
 * DTO (Data Transfer Object) para representar la respuesta de una solicitud de retención de asientos en el sistema CINEMAX.
 * Este DTO se utiliza para enviar la información de los asientos que han sido bloqueados, el ID de la función
 * correspondiente y la fecha y hora de expiración de la retención al cliente.
 */
public record HoldAsientosResponseDTO(
        Long funcionId,
        List<Long> bloqueados,
        LocalDateTime expiraEn
) {}