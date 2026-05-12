package com.uniquindio.CINEMAX.negocio.DTO;

/**
 * DTO para solicitar la creación de un checkout indicando el proveedor de pago.
 */
public record CrearCheckoutPagoRequestDTO(
        String proveedor
) {}