package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResendVerificationDTO(
        @NotBlank @Email @Size(max = 180)
        String email
) {}