package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDate;
import java.util.Set;

public record UserMeDTO(
        Long id,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        String email,
        String telefono,
        boolean emailVerificado,
        String estado,
        Set<String> roles
) {}