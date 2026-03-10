package com.uniquindio.CINEMAX.config;
/**
 * Configuración de Cloudinary para la aplicación CINEMAX.
 * Esta clase define un bean de Cloudinary que se configura utilizando las
 * propiedades definidas en el archivo de configuración de la aplicación (application.properties o application.yml).
 * Las propiedades necesarias son:
 * - cloudinary.cloud-name: El nombre de la nube de Cloudinary.
 * - cloudinary.api-key: La clave de API de Cloudinary.
 * - cloudinary.api-secret: El secreto de API de Cloudinary.
 */

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}