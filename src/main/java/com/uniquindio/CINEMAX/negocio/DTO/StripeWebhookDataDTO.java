package com.uniquindio.CINEMAX.negocio.DTO;

/**
 * DTO interno para representar los datos relevantes de un webhook de Stripe.
 */
public record StripeWebhookDataDTO(
        String eventType,
        String sessionId,
        String paymentIntentId,
        String paymentStatus,
        String referencia
) {}