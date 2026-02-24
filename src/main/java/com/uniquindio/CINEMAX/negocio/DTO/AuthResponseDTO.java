package com.uniquindio.CINEMAX.negocio.DTO;

public record AuthResponseDTO(
        String accessToken,
        String tokenType,   // "Bearer"
        long expiresIn,     // segundos
        UserSummaryDTO user
) {}