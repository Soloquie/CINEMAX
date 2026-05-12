package com.uniquindio.CINEMAX.negocio.DTO;

import java.math.BigDecimal;

public record CompraResumenDTO(
        Long ventaId,
        String codigoVenta,
        String estadoVenta,
        BigDecimal total,
        String referenciaPago,
        String estadoPago
) {}