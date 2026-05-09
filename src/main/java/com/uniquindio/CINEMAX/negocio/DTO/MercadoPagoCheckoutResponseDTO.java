package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) que representa la respuesta para iniciar un checkout con Mercado Pago.
 * Contiene la URL a la que el frontend debe redirigir al usuario para finalizar el pago.
 */
public record MercadoPagoCheckoutResponseDTO(
        String referencia,
        String preferenceId,
        String initPoint,
        String publicKey
) {}