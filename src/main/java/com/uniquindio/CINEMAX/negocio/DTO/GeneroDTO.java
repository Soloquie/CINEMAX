package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) para representar la información de un género en el sistema CINEMAX.
 * Este DTO se utiliza para enviar la información básica del género al cliente, incluyendo su ID y nombre.
 */
public record GeneroDTO(Long id, String nombre) {}