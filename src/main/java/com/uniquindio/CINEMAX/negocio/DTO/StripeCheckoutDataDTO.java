package com.uniquindio.CINEMAX.negocio.DTO;

/**
 * DTO interno con los datos generados por Stripe Checkout.
 */
public record StripeCheckoutDataDTO(
        String sessionId,
        String url
) {}