package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) para representar la respuesta de autenticación en el sistema CINEMAX.
 * Este DTO se utiliza para enviar la información del token de acceso, tipo de token, tiempo de expiración y
 * resumen del usuario al cliente después de un proceso de autenticación exitoso.
 */
public record AuthResponseDTO(
        String accessToken,
        String tokenType,   // "Bearer"
        long expiresIn,     // segundos
        UserSummaryDTO user
) {}