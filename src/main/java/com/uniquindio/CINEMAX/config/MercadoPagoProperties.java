package com.uniquindio.CINEMAX.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/* Clase de configuración para centralizar las propiedades relacionadas con Mercado Pago.
 * Permite leer las credenciales de prueba, URLs de retorno, URL de notificación y moneda
 * sin exponer valores sensibles dentro del código fuente.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.mercadopago")
public class MercadoPagoProperties {

    private String publicKey;
    private String accessToken;
    private String successUrl;
    private String failureUrl;
    private String pendingUrl;
    private String notificationUrl;
    private String currency = "COP";
    private String environment = "test";
}