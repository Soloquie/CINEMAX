package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) que representa el estado actual de un pago y su venta asociada.
 * Es utilizado por el frontend para consultar si el pago sigue pendiente, fue aprobado,
 * fue rechazado o ya generó una venta confirmada.
 */
public record PagoEstadoResponseDTO(
        String referencia,
        String estadoPago,
        String estadoVenta,
        Long ventaId,
        String codigoVenta,
        String mensaje
) {}