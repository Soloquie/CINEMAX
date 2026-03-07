package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record HoldAsientosRequestDTO(
        @NotEmpty List<Long> funcionAsientoIds
) {}