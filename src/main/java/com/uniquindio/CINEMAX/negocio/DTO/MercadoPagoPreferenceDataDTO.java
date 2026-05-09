package com.uniquindio.CINEMAX.negocio.DTO;
/**
 * DTO interno para representar los datos retornados por Mercado Pago al crear una preferencia.
 * Contiene el identificador de la preferencia y los enlaces de inicio del checkout.
 */
public record MercadoPagoPreferenceDataDTO(
        String preferenceId,
        String initPoint,
        String sandboxInitPoint
) {}