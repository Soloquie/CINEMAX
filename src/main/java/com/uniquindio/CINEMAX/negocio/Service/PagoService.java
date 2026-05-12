package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.CheckoutPagoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.CrearCheckoutPagoRequestDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PagoEstadoResponseDTO;

import java.util.Map;

/**
 * Interfaz de servicio para gestionar pagos en CINEMAX.
 */
public interface PagoService {

    CheckoutPagoResponseDTO crearCheckout(String userEmail, CrearCheckoutPagoRequestDTO request);

    PagoEstadoResponseDTO consultarEstado(String referencia, String userEmail);

    void procesarWebhookMercadoPago(Map<String, Object> body, Map<String, String> params);

    void procesarWebhookStripe(String payload, String signature);
}