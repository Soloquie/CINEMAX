package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) para representar una respuesta de mensaje en el sistema CINEMAX.
 * Este DTO se utiliza para enviar un mensaje de texto al cliente, generalmente como resultado de una
 * operación exitosa o para proporcionar información relevante.
 */
public record MessageResponseDTO(
        String message
) {}