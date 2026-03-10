package com.uniquindio.CINEMAX.Seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
/* Configuración de CORS para permitir solicitudes desde el frontend (Angular) hacia el backend (Spring Boot).
 * Esta clase define una configuración de CORS personalizada que especifica los orígenes permitidos, los métodos HTTP permitidos,
 * los encabezados permitidos y si se permiten las credenciales. Esto es esencial para habilitar la comunicación
 *  entre el frontend y el backend sin problemas,  especialmente cuando están alojados en dominios diferentes
 *  durante el desarrollo.
   */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        // Si NO usas cookies/sesión (solo JWT en Authorization), puede ser false
        config.setAllowCredentials(false);

        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}