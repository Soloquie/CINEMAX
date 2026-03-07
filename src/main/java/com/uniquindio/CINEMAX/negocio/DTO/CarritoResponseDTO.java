package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record CarritoResponseDTO(
        Long carritoId,
        String estado,
        LocalDateTime expiraEn,
        List<CarritoItemResponseDTO> items
) {}