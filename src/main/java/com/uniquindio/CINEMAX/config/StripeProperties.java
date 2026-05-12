package com.uniquindio.CINEMAX.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propiedades de configuración para la integración con Stripe Checkout.
 * Las llaves deben configurarse mediante variables de entorno y no deben quedar quemadas en el código.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.stripe")
public class StripeProperties {

    private String publicKey;
    private String secretKey;
    private String successUrl;
    private String cancelUrl;
    private String webhookSecret;
    private String currency = "cop";
}