package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * DTO (Data Transfer Object) que representa una boleta generada después de una venta confirmada.
 * Contiene información de la película, cine, sala, asiento, función y precio pagado.
 */
public record BoletaResponseDTO(
        Long id,
        String codigo,
        String pelicula,
        String cine,
        String sala,
        String fila,
        Integer numero,
        LocalDateTime inicioFuncion,
        BigDecimal precio
) {}