package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.*;

public record LoginRequestDTO(
        @NotBlank @Email @Size(max = 180)
        String email,

        @NotBlank @Size(min = 8, max = 72)
        String password
) {}