package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * DTO (Data Transfer Object) para representar la información necesaria para crear o actualizar un cine
 * en el sistema CINEMAX. Este DTO se utiliza para recibir los datos del cine desde el cliente, incluyendo su nombre,
 * ciudad, dirección y estado de actividad. Contiene validaciones para asegurar que el nombre, la ciudad y la dirección
 * no estén vacíos y que cumplan con las restricciones de longitud establecidas.
 */
public record CineUpsertDTO(
        @NotBlank @Size(max = 120) String nombre,
        @NotBlank @Size(max = 80) String ciudad,
        @NotBlank @Size(max = 180) String direccion,
        Boolean activo
) {}