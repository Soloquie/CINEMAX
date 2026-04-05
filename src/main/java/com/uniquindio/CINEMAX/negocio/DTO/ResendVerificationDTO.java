package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * DTO (Data Transfer Object) para representar la solicitud de reenvío de verificación de correo electrónico en
 * el sistema CINEMAX. Este DTO se utiliza para recibir el correo electrónico del usuario que desea recibir
 * nuevamente el enlace de verificación, con validaciones para asegurar que el correo electrónico sea válido y cumpla
 * con las restricciones de longitud establecidas.
 */
public record ResendVerificationDTO(
        @NotBlank @Email @Size(max = 180)
        String email
) {}