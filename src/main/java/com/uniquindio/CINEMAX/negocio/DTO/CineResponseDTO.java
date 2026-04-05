package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) para representar la información detallada de un cine en el sistema CINEMAX.
 * Este DTO se utiliza para enviar la información completa del cine al cliente, incluyendo su ID, nombre,
 * ciudad, dirección y estado de actividad.
 */
public record CineResponseDTO(
        Long id,
        String nombre,
        String ciudad,
        String direccion,
        Boolean activo
) {}