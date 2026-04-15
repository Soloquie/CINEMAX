package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RemoveCartItemsRequestDTO(
        @NotEmpty(message = "Debes enviar al menos un asiento")
        List<@NotNull(message = "El id del asiento no puede ser nulo") Long> funcionAsientoIds
) {}