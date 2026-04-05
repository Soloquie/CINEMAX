package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.*;
/**
 * DTO (Data Transfer Object) para representar la solicitud de inicio de sesión en el sistema CINEMAX.
 * Este DTO se utiliza para recibir el correo electrónico y la contraseña del usuario que intenta iniciar sesión,
 * con validaciones para asegurar que el correo electrónico sea válido y que la contraseña cumpla con las restricciones
 * de longitud establecidas.
 */
public record LoginRequestDTO(
        @NotBlank @Email @Size(max = 180)
        String email,

        @NotBlank @Size(min = 8, max = 72)
        String password
) {}