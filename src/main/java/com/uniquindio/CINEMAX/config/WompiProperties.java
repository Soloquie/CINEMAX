package com.uniquindio.CINEMAX.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/* Clase de configuración para centralizar las propiedades relacionadas con Wompi.
 * Permite leer desde application-dev.properties o application-prod.properties las llaves,
 * ambiente, moneda y URL de redirección sin exponer valores sensibles dentro del código.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.wompi")
public class WompiProperties {

    private String environment;
    private String publicKey;
    private String privateKey;
    private String integritySecret;
    private String redirectUrl;
    private String currency = "COP";
}