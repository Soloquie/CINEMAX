package com.uniquindio.CINEMAX.negocio.DTO;

import java.util.Set;

public record UserSummaryDTO(
        Long id,
        String nombre,
        String apellido,
        String email,
        boolean emailVerificado,
        String estado,
        Set<String> roles
) {}