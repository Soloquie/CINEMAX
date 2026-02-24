package com.uniquindio.CINEMAX.negocio.Model;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoToken;

import java.time.LocalDateTime;

public record TokenUsuario(
        Long id,
        Long usuarioId,
        String token,
        TipoToken tipo,
        LocalDateTime creadoEn,
        LocalDateTime expiraEn,
        LocalDateTime usadoEn
) {}