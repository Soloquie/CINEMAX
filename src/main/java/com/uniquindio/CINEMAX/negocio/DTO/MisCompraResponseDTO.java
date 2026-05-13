package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDateTime;

public record MisCompraResponseDTO(
        Long ventaId,
        String codigoVenta,
        String referenciaPago,
        Long montoCentavos,
        String estadoPago,
        String estadoVenta,
        LocalDateTime fechaCompra
) {}