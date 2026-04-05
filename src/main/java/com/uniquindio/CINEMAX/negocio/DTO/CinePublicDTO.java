package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) para representar la información pública de un cine en el sistema CINEMAX.
 * Este DTO se utiliza para enviar la información básica del cine al cliente, incluyendo su ID, nombre,
 * ciudad y dirección.
 */
public record CinePublicDTO(Long id, String nombre, String ciudad, String direccion) {}