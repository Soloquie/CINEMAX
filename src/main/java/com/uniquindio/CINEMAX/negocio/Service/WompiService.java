package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.Persistencia.Entity.PagoEntity;
import com.uniquindio.CINEMAX.negocio.DTO.WompiCheckoutDataDTO;
import com.uniquindio.CINEMAX.negocio.DTO.WompiWebhookDTO;
/* Interfaz de servicio para gestionar operaciones propias del proveedor de pagos Wompi.
 * Define métodos para generar firmas de integridad, construir datos de checkout
 * y extraer información relevante de los webhooks recibidos.
 */
public interface WompiService {

    String generarFirmaIntegridad(String referencia, Long montoCentavos, String moneda);

    WompiCheckoutDataDTO construirCheckout(PagoEntity pago);

    String extraerReferencia(WompiWebhookDTO webhook);

    String extraerTransactionId(WompiWebhookDTO webhook);

    String extraerEstadoTransaccion(WompiWebhookDTO webhook);

    String payloadToJson(WompiWebhookDTO webhook);
}