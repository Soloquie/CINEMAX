package com.uniquindio.CINEMAX.Persistencia.Entity;
/* Enumeración que representa los tipos de tokens utilizados en el sistema CINEMAX para funcionalidades específicas.
 * Los tipos incluyen VERIFICAR_EMAIL (token utilizado para la verificación de correo electrónico durante el registro)
 * y RESET_PASSWORD (token utilizado para restablecer la contraseña del usuario).
  */
public enum TipoToken {
    VERIFICAR_EMAIL,
    RESET_PASSWORD
}