package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FuncionResponseDTO(
        Long id,
        Long peliculaId,
        String peliculaTitulo,
        String posterUrl,
        Long salaId,
        String salaNombre,
        Long cineId,
        String cineNombre,
        LocalDateTime inicio,
        LocalDateTime fin,
        String idioma,
        Boolean subtitulos,
        BigDecimal precioBase,
        String estado
) {}