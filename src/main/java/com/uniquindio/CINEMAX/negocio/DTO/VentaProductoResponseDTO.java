package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;
/**
 * DTO (Data Transfer Object) que representa un producto de confitería incluido en una venta confirmada.
 * Incluye el producto, nombre histórico, cantidad, precio unitario y subtotal.
 */
public record VentaProductoResponseDTO(
        Long productoId,
        String nombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}