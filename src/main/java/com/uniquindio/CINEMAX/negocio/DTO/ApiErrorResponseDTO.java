package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDateTime;
/**
 * DTO (Data Transfer Object) para representar respuestas uniformes de error en la API del sistema CINEMAX.
 * Este DTO incluye la fecha y hora del error, el estado HTTP, el tipo de error, el código funcional,
 * el mensaje descriptivo y la ruta donde ocurrió la excepción.
 */
public record ApiErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path
) {}