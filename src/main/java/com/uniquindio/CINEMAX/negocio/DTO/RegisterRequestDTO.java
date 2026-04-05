package com.uniquindio.CINEMAX.negocio.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
/**
 * DTO (Data Transfer Object) para representar la solicitud de registro de un nuevo usuario en el sistema CINEMAX.
 * Este DTO se utiliza para recibir la información necesaria para crear una nueva cuenta de usuario, incluyendo
 * nombre, apellido, fecha de nacimiento, correo electrónico, teléfono y contraseña, con validaciones para asegurar que
 * los datos sean correctos y cumplan con las restricciones establecidas.
 */
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