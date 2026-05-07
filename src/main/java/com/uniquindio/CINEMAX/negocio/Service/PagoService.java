package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.CheckoutPagoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PagoEstadoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.WompiWebhookDTO;
/* Interfaz de servicio para gestionar el flujo de pagos en el sistema CINEMAX.
 * Incluye la creación del checkout, consulta de estado y procesamiento de eventos webhook.
 */
public interface PagoService {

    CheckoutPagoResponseDTO crearCheckout(String userEmail);

    PagoEstadoResponseDTO consultarEstado(String referencia, String userEmail);

    void procesarWebhookWompi(WompiWebhookDTO webhook);
}