package com.uniquindio.CINEMAX.negocio.DTO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoSala;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
/**
 * DTO (Data Transfer Object) para representar la información necesaria para crear o actualizar una sala de cine en el
 * sistema CINEMAX. Este DTO se utiliza para recibir la información del nombre de la sala, el tipo de sala y su estado
 * de actividad. Contiene validaciones para asegurar que el nombre no esté vacío,
 * que tenga un tamaño máximo de 60 caracteres, y que el tipo de sala no sea nulo.
 */
public record SalaUpsertDTO(
        @NotBlank @Size(max = 60) String nombre,
        @NotNull TipoSala tipo,
        Boolean activa
) {}