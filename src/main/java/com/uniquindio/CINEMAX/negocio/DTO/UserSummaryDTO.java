package com.uniquindio.CINEMAX.negocio.DTO;

import java.util.Set;
/**
 * DTO (Data Transfer Object) para representar un resumen de la información de un usuario en el sistema CINEMAX.
 * Este DTO se utiliza para enviar información básica del usuario al cliente, incluyendo su ID, nombre, apellido,
 * email, estado de verificación del email, estado de la cuenta y los roles que tiene asignados.
 */
public record UserSummaryDTO(
        Long id,
        String nombre,
        String apellido,
        String email,
        boolean emailVerificado,
        String estado,
        Set<String> roles
) {}