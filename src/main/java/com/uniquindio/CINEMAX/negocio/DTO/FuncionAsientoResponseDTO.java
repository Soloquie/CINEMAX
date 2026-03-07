package com.uniquindio.CINEMAX.negocio.DTO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoAsiento;

import java.time.LocalDateTime;

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