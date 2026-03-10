package com.uniquindio.CINEMAX.negocio.Model;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoToken;

import java.time.LocalDateTime;
/**
 * Modelo de dominio para representar un token de usuario en el sistema CINEMAX.
 * Este modelo se utiliza para almacenar y gestionar la información relacionada con los tokens de autenticación,
 * incluyendo su ID, ID del usuario asociado, valor del token, tipo de token, fecha de creación, fecha de expiración
 * y fecha de uso del token.
 */
public record TokenUsuario(
        Long id,
        Long usuarioId,
        String token,
        TipoToken tipo,
        LocalDateTime creadoEn,
        LocalDateTime expiraEn,
        LocalDateTime usadoEn
) {}