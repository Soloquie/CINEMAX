package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record RegisterRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 80, message = "El apellido debe tener entre 2 y 80 caracteres")
        String apellido,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        LocalDate fechaNacimiento,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Debes ingresar un correo válido")
        @Size(max = 180, message = "El correo no puede exceder 180 caracteres")
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^[0-9]{7,15}$", message = "El teléfono debe tener entre 7 y 15 dígitos")
        String telefono,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        String password
) {}