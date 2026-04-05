package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDate;
import java.util.Set;
/**
 * DTO (Data Transfer Object) para representar la información del usuario autenticado en el sistema CINEMAX.
 * Este DTO se utiliza para enviar la información del usuario al cliente después de la autenticación,
 * incluyendo su ID, nombre, apellido, fecha de nacimiento, email, teléfono, estado de verificación del email,
 * estado de la cuenta y los roles que tiene asignados.
 */
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