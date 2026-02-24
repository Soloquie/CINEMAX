package com.uniquindio.CINEMAX.negocio.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequestDTO(
        @NotBlank @Size(min = 2, max = 80)
        String nombre,

        @NotBlank @Size(min = 2, max = 80)
        String apellido,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fechaNacimiento,

        @NotBlank @Email @Size(max = 180)
        String email,

        @Size(max = 30)
        String telefono,

        @NotBlank @Size(min = 8, max = 72)
        String password
) {}