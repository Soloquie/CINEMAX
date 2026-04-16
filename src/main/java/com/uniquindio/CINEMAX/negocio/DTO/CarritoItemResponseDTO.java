package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CarritoItemResponseDTO(
        Long itemId,
        String tipo,

        // asientos
        Long funcionAsientoId,
        Long funcionId,
        String peliculaTitulo,
        String salaNombre,
        String cineNombre,
        LocalDateTime inicioFuncion,
        String fila,
        Integer numero,

        // productos
        Long productoId,
        String productoNombre,
        String productoDescripcion,
        String productoImagenUrl,
        Integer cantidad,

        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}