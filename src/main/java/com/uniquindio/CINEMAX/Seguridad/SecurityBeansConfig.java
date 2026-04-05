package com.uniquindio.CINEMAX.Seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/* Configuración de beans relacionados con la seguridad en la aplicación CINEMAX.
 * Esta clase define un bean de PasswordEncoder utilizando BCrypt, que es un algoritmo de hashing seguro para almacenar contraseñas.
 * Al configurar este bean, se asegura que las contraseñas de los usuarios se almacenen de manera segura en la base de datos,
 * protegiendo la información sensible y mejorando la seguridad general del sistema.
   */
@Configuration
public class SecurityBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}