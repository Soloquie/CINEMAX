package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.MercadoPagoCheckoutResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PagoEstadoResponseDTO;

import java.util.Map;

/* Interfaz de servicio para gestionar el flujo de pagos en el sistema CINEMAX.
 * Incluye la creación del checkout, consulta de estado y procesamiento de eventos webhook.
 */
public interface PagoService {

    MercadoPagoCheckoutResponseDTO crearCheckout(String userEmail);

    PagoEstadoResponseDTO consultarEstado(String referencia, String userEmail);

    void procesarWebhookMercadoPago(Map<String, Object> body, Map<String, String> params);
}