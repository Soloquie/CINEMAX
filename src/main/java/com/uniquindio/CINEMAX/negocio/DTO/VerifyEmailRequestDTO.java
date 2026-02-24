package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyEmailRequestDTO(
        @NotBlank @Size(min = 20, max = 255)
        String token
) {}