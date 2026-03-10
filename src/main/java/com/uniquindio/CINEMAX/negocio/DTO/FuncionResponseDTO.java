package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * DTO (Data Transfer Object) para representar la información detallada de una función en el sistema CINEMAX.
 * Este DTO se utiliza para enviar la información completa de una función al cliente, incluyendo detalles de la
 * película, cine, sala, horarios, idioma, subtítulos, precio base y estado.
 */
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