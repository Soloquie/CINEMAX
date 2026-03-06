package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank @Size(min = 20, max = 255)
        String token,

        @NotBlank @Size(min = 8, max = 72)
        String newPassword
) {}