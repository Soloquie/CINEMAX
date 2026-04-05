package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * DTO (Data Transfer Object) para representar la información necesaria para verificar el correo electrónico de un
 * usuario en el sistema CINEMAX. Este DTO se utiliza para recibir el token de verificación que se envía
 * al correo electrónico del usuario durante el proceso de registro o recuperación de contraseña.
 * Contiene validaciones para asegurar que el token no esté vacío y que tenga un tamaño entre 20 y 255 caracteres.
 */
public record VerifyEmailRequestDTO(
        @NotBlank @Size(min = 20, max = 255)
        String token
) {}