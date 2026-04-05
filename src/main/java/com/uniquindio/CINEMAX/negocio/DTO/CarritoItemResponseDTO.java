package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * DTO (Data Transfer Object) para representar la información de un ítem en el carrito de compras del sistema CINEMAX.
 * Este DTO se utiliza para enviar la información detallada de cada ítem en el carrito, incluyendo su ID,
 * tipo (asiento o combo), detalles de la función y asiento (si aplica), y el precio unitario.
 */
public record CarritoItemResponseDTO(
        Long itemId,
        String tipo,

        Long funcionAsientoId,
        Long funcionId,
        String peliculaTitulo,
        String salaNombre,
        String cineNombre,
        LocalDateTime inicioFuncion,
        String fila,
        Integer numero,

        BigDecimal precioUnitario
) {}