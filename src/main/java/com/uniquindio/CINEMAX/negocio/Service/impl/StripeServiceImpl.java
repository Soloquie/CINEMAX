package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.uniquindio.CINEMAX.config.StripeProperties;
import com.uniquindio.CINEMAX.Persistencia.Entity.PagoEntity;
import com.uniquindio.CINEMAX.negocio.DTO.StripeCheckoutDataDTO;
import com.uniquindio.CINEMAX.negocio.DTO.StripeWebhookDataDTO;
import com.uniquindio.CINEMAX.negocio.Exception.PagoInvalidoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de Stripe para crear sesiones de Checkout
 * y procesar webhooks firmados por Stripe.
 */
@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements com.uniquindio.CINEMAX.negocio.Service.StripeService {

    private final StripeProperties stripeProperties;

    /**
     * Crea una sesión de Stripe Checkout para redirigir al usuario al formulario de pago externo.
     */
    @Override
    public StripeCheckoutDataDTO crearSesionCheckout(PagoEntity pago) {
        try {
            Stripe.apiKey = getSecretKeySeguro();

            String successUrl = construirSuccessUrl(pago.getReferencia());
            String cancelUrl = construirCancelUrl(pago.getReferencia());

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setClientReferenceId(pago.getReferencia())
                    .putMetadata("referencia", pago.getReferencia())
                    .putMetadata("pagoId", String.valueOf(pago.getId()))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(stripeProperties.getCurrency())
                                                    .setUnitAmount(pago.getMontoCentavos())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Compra CINEMAX")
                                                                    .setDescription("Boletas y productos de confitería")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            return new StripeCheckoutDataDTO(
                    session.getId(),
                    session.getUrl()
            );

        } catch (Exception e) {
            throw new PagoInvalidoException("No se pudo crear la sesión de Stripe: " + e.getMessage());
        }
    }

    /**
     * Procesa el webhook de Stripe validando la firma enviada por el proveedor.
     */
    @Override
    public StripeWebhookDataDTO procesarWebhook(String payload, String signature) {
        try {
            Event event = Webhook.constructEvent(
                    payload,
                    signature,
                    getWebhookSecretSeguro()
            );

            String eventType = event.getType();

            /*
             * Stripe envía muchos eventos relacionados con el pago.
             * Para CINEMAX solo necesitamos leer la Session cuando sea un evento de checkout.
             */
            if (!"checkout.session.completed".equals(eventType)
                    && !"checkout.session.expired".equals(eventType)) {
                return new StripeWebhookDataDTO(
                        eventType,
                        null,
                        null,
                        null,
                        null
                );
            }

            StripeObject stripeObject = event.getDataObjectDeserializer()
                    .getObject()
                    .orElseGet(() -> {
                        try {
                            return event.getDataObjectDeserializer().deserializeUnsafe();
                        } catch (Exception e) {
                            throw new PagoInvalidoException("No se pudo deserializar la sesión de Stripe: " + e.getMessage());
                        }
                    });

            if (!(stripeObject instanceof Session session)) {
                throw new PagoInvalidoException("El evento de Stripe no corresponde a una sesión de checkout.");
            }

            String referencia = session.getClientReferenceId();

            if ((referencia == null || referencia.isBlank()) && session.getMetadata() != null) {
                referencia = session.getMetadata().get("referencia");
            }

            String paymentIntentId = session.getPaymentIntent();
            String paymentStatus = session.getPaymentStatus();

            return new StripeWebhookDataDTO(
                    eventType,
                    session.getId(),
                    paymentIntentId,
                    paymentStatus,
                    referencia
            );

        } catch (PagoInvalidoException e) {
            throw e;
        } catch (Exception e) {
            throw new PagoInvalidoException("Webhook de Stripe inválido: " + e.getMessage());
        }
    }

    private String getSecretKeySeguro() {
        String key = stripeProperties.getSecretKey();

        if (key == null || key.isBlank()) {
            throw new PagoInvalidoException("No se configuró la secret key de Stripe.");
        }

        if (!key.startsWith("sk_test_") && !key.startsWith("sk_live_")) {
            throw new PagoInvalidoException("La secret key de Stripe tiene un formato inválido.");
        }

        return key.trim();
    }

    private String getWebhookSecretSeguro() {
        String secret = stripeProperties.getWebhookSecret();

        if (secret == null || secret.isBlank()) {
            throw new PagoInvalidoException("No se configuró el webhook secret de Stripe.");
        }

        return secret.trim();
    }

    private String construirSuccessUrl(String referencia) {
        String base = stripeProperties.getSuccessUrl();

        if (base == null || base.isBlank()) {
            throw new PagoInvalidoException("No se configuró la success-url de Stripe.");
        }

        String separador = base.contains("?") ? "&" : "?";

        /*
         * Stripe reemplaza {CHECKOUT_SESSION_ID} automáticamente por el ID real de la sesión.
         */
        return base.trim()
                + separador
                + "referencia=" + referencia
                + "&session_id={CHECKOUT_SESSION_ID}";
    }

    private String construirCancelUrl(String referencia) {
        String base = stripeProperties.getCancelUrl();

        if (base == null || base.isBlank()) {
            throw new PagoInvalidoException("No se configuró la cancel-url de Stripe.");
        }

        String separador = base.contains("?") ? "&" : "?";

        return base.trim()
                + separador
                + "referencia=" + referencia
                + "&cancelado=true";
    }
}