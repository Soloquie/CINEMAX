package com.uniquindio.CINEMAX.negocio.DTO;

public record CineResponseDTO(
        Long id,
        String nombre,
        String ciudad,
        String direccion,
        Boolean activo
) {}