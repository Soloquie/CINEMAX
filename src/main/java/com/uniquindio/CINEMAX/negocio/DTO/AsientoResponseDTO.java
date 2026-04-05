package com.uniquindio.CINEMAX.negocio.DTO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoAsiento;
/**
 * DTO (Data Transfer Object) para representar la información de un asiento en una función de cine.
 * Este DTO se utiliza para enviar la información del asiento al cliente, incluyendo su ID, fila,
 * número, tipo y estado de actividad.
 */
public record AsientoResponseDTO(
        Long id,
        String fila,
        Integer numero,
        TipoAsiento tipo,
        Boolean activo
) {}