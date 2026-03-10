package com.uniquindio.CINEMAX;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
/* Clase de prueba para la aplicación CINEMAX, anotada con @SpringBootTest para indicar que se trata de una prueba de
 integración que carga el contexto completo de Spring Boot. Esta clase contiene un método de prueba llamado
 *contextLoads, que verifica que el contexto de la aplicación se cargue correctamente sin errores.
 * Es una prueba básica que asegura que la configuración general de la aplicación es válida y que los
  *componentes necesarios se inicializan correctamente.
   */
@SpringBootTest
class CinemaxApplicationTests {

	@Test
	void contextLoads() {
	}

}
