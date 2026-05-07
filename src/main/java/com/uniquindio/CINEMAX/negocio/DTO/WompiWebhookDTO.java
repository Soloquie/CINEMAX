package com.uniquindio.CINEMAX.negocio.DTO;

import java.util.Map;
/**
 * DTO (Data Transfer Object) flexible para recibir eventos webhook enviados por Wompi.
 * Se define con mapas para permitir procesar el payload externo sin acoplar inicialmente el sistema
 * a una estructura rígida del proveedor.
 */
public record WompiWebhookDTO(
        String event,
        Map<String, Object> data,
        Map<String, Object> signature,
        Long timestamp
) {}