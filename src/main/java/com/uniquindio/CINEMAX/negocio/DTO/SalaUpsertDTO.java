package com.uniquindio.CINEMAX.negocio.DTO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoSala;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SalaUpsertDTO(
        @NotBlank @Size(max = 60) String nombre,
        @NotNull TipoSala tipo,
        Boolean activa
) {}