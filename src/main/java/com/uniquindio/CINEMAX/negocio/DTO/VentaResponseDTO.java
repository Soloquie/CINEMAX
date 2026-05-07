package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;
import java.util.List;
/**
 * DTO (Data Transfer Object) que representa la información completa de una venta confirmada.
 * Incluye el identificador de la venta, código, estado, total, boletas generadas y productos de confitería vendidos.
 */
public record VentaResponseDTO(
        Long ventaId,
        String codigoVenta,
        String estado,
        BigDecimal total,
        List<BoletaResponseDTO> boletas,
        List<VentaProductoResponseDTO> productos
) {}