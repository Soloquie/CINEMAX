package com.uniquindio.CINEMAX.negocio.Service;

import com.mercadopago.resources.payment.Payment;
import com.uniquindio.CINEMAX.Persistencia.Entity.PagoEntity;
import com.uniquindio.CINEMAX.negocio.DTO.MercadoPagoPreferenceDataDTO;

import java.util.Map;
/* Interfaz de servicio para gestionar la integración con Mercado Pago.
 * Define la creación de preferencias de Checkout Pro, la consulta de pagos
 * y la extracción de datos desde los webhooks recibidos.
 */
public interface MercadoPagoService {

    MercadoPagoPreferenceDataDTO crearPreferencia(PagoEntity pago);

    String extraerPaymentId(Map<String, Object> body, Map<String, String> params);

    Payment obtenerPago(String paymentId);

    String payloadToJson(Map<String, Object> body, Map<String, String> params);
}