package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.Persistencia.Entity.PagoEntity;
import com.uniquindio.CINEMAX.negocio.DTO.StripeCheckoutDataDTO;
import com.uniquindio.CINEMAX.negocio.DTO.StripeWebhookDataDTO;

/**
 * Servicio para integrar CINEMAX con Stripe Checkout.
 */
public interface StripeService {

    StripeCheckoutDataDTO crearSesionCheckout(PagoEntity pago);

    StripeWebhookDataDTO procesarWebhook(String payload, String signature);
}