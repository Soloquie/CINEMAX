package com.uniquindio.CINEMAX;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/* Clase principal de la aplicación CINEMAX, anotada con @SpringBootApplication para indicar que es una aplicación Spring Boot.
 * Esta clase contiene el método main, que es el punto de entrada de la aplicación. Al ejecutar este método, se inicia
 * el contexto de Spring Boot,  lo que permite que la aplicación se ejecute y esté lista para manejar solicitudes.
 * La anotación @SpringBootApplication también habilita la configuración automática y  el escaneo de componentes
   */
@SpringBootApplication
public class CinemaxApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaxApplication.class, args);
	}

}
