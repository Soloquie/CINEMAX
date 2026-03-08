package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FuncionPublicDTO(
        Long id,
        Long peliculaId,
        String peliculaTitulo,
        String posterUrl,
        Long cineId,
        String cineNombre,
        Long salaId,
        String salaNombre,
        LocalDateTime inicio,
        LocalDateTime fin,
        String idioma,
        boolean subtitulos,
        BigDecimal precioBase,
        String estado
) {}