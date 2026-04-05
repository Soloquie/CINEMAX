package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * DTO (Data Transfer Object) para representar la información pública de una función en el sistema CINEMAX.
 * Este DTO se utiliza para enviar la información básica de una función al cliente, incluyendo detalles de la película,
 * cine, sala, horarios, idioma, subtítulos, precio base y estado.
 */
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