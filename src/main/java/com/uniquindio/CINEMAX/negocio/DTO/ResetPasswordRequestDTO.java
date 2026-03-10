package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * DTO (Data Transfer Object) para representar la solicitud de restablecimiento de contraseña en el sistema CINEMAX.
 * Este DTO se utiliza para recibir el token de restablecimiento de contraseña y la nueva contraseña
 * que el usuario desea establecer, con validaciones para asegurar que ambos campos no estén vacíos y cumplan con las restricciones de longitud establecidas.
 */
public record ResetPasswordRequestDTO(
        @NotBlank @Size(min = 20, max = 255)
        String token,

        @NotBlank @Size(min = 8, max = 72)
        String newPassword
) {}