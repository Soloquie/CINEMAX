package com.uniquindio.CINEMAX.negocio.DTO;

/**
 * DTO que representa la respuesta para iniciar un checkout de pago.
 * Este DTO es genérico para soportar diferentes proveedores como Mercado Pago y Stripe.
 * El campo initPoint contiene la URL externa a la que el frontend debe redirigir al usuario.
 */
public record CheckoutPagoResponseDTO(
        String referencia,
        String proveedor,
        Long montoCentavos,
        String moneda,
        String checkoutId,
        String initPoint,
        String publicKey
) {}