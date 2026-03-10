package com.uniquindio.CINEMAX.negocio.Model;

import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoUsuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
/**
 * Modelo de dominio para representar un usuario en el sistema CINEMAX.
 * Este modelo se utiliza para almacenar y gestionar la información relacionada con los usuarios,
 * incluyendo su ID, nombre, apellido, fecha de nacimiento, email, teléfono, hash de contraseña,
 * estado de verificación del email, estado de la cuenta, fecha del último login, roles asignados,
 * intentos fallidos de login y fecha hasta la cual el usuario está bloqueado (si aplica).
 */
public record Usuario(
        Long id,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        String email,
        String telefono,
        String passwordHash,
        boolean emailVerificado,
        EstadoUsuario estado,
        LocalDateTime ultimoLoginEn,
        Set<String> roles,
        Integer intentosFallidos,
        LocalDateTime bloqueadoHasta
) {}