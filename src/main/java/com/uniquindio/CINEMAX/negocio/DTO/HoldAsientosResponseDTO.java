package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record HoldAsientosResponseDTO(
        Long funcionId,
        List<Long> bloqueados,
        LocalDateTime expiraEn
) {}