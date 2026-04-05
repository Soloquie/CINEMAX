package com.uniquindio.CINEMAX.negocio.DTO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoAsiento;

import java.time.LocalDateTime;
/**
 * DTO (Data Transfer Object) para representar la información de un asiento en una función específica en el
 * sistema CINEMAX. Este DTO se utiliza para enviar la información detallada del asiento reservado
 * o disponible en una función, incluyendo su ID, fila, número, tipo, estado de reserva, si es propiedad del usuario.
 */
public record FuncionAsientoResponseDTO(
        Long funcionAsientoId,
        Long asientoId,
        String fila,
        Integer numero,
        TipoAsiento tipo,
        String estado,
        boolean mio,
        LocalDateTime retencionExpira
) {}