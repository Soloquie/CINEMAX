package com.uniquindio.CINEMAX.negocio.DTO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoSala;
/**
 * DTO (Data Transfer Object) para representar la respuesta de información de una sala en el sistema CINEMAX.
 * Este DTO se utiliza para enviar al cliente la información detallada de una sala, incluyendo su ID, el ID y
 * nombre del cine al que pertenece, su nombre, tipo y estado de actividad, permitiendo así una representación completa
 * de la sala para su visualización o uso en otras operaciones dentro del sistema.
 */
public record SalaResponseDTO(
        Long id,
        Long cineId,
        String cineNombre,
        String nombre,
        TipoSala tipo,
        Boolean activa
) {}