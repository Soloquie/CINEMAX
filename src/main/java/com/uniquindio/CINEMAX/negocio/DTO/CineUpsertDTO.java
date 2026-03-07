package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CineUpsertDTO(
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Size(max = 80) String ciudad,
        @NotBlank @Size(max = 180) String direccion,
        Boolean activo
) {}