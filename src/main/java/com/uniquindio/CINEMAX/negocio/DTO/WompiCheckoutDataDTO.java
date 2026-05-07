package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO (Data Transfer Object) interno para representar los datos necesarios del checkout de Wompi.
 * Este objeto facilita separar la construcción de datos del proveedor respecto al flujo principal de pagos.
 */
public record WompiCheckoutDataDTO(
        String publicKey,
        String reference,
        Long amountInCents,
        String currency,
        String integritySignature,
        String redirectUrl,
        String environment
) {}