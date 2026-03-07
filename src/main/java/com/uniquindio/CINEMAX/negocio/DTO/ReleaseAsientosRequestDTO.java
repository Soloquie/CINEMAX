package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ReleaseAsientosRequestDTO(
        @NotEmpty List<Long> funcionAsientoIds
) {}