package com.uniquindio.CINEMAX.negocio.Model;

import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoUsuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

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
        Set<String> roles // {"CLIENTE","ADMIN"}
) {}