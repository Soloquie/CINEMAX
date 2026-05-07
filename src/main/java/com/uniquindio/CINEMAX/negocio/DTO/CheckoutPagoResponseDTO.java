package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) que representa la respuesta para iniciar un checkout de pago.
 * Contiene los datos necesarios para que el frontend pueda abrir el widget o checkout del proveedor externo.
 */
public record CheckoutPagoResponseDTO(
        String referencia,
        Long montoCentavos,
        String moneda,
        String publicKey,
        String integritySignature,
        String redirectUrl,
        String environment,
        String customerEmail
) {}