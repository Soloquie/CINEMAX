package com.uniquindio.CINEMAX.Persistencia.Entity;
/* Enumeración que representa los posibles estados de un usuario en el sistema CINEMAX.
 * Los estados incluyen ACTIVO (usuario con acceso normal al sistema),
 * BLOQUEADO (usuario bloqueado temporalmente por razones de seguridad o comportamiento)
 * e INACTIVO (usuario que no ha sido activado o que ha sido desactivado permanentemente).
  */
public enum EstadoUsuario {
    ACTIVO,
    BLOQUEADO,
    INACTIVO
}