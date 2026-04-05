package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) para representar la información pública de una sala en el sistema CINEMAX.
 * Este DTO se utiliza para enviar al cliente la información básica de una sala, como su ID, nombre y tipo,
 * sin incluir detalles sensibles o internos que no sean necesarios para la visualización pública.
 */
public record SalaPublicDTO(Long id, String nombre, String tipo) {}