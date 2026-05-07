package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniquindio.CINEMAX.config.   WompiProperties;
import com.uniquindio.CINEMAX.Persistencia.Entity.PagoEntity;
import com.uniquindio.CINEMAX.negocio.DTO.WompiCheckoutDataDTO;
import com.uniquindio.CINEMAX.negocio.DTO.WompiWebhookDTO;
import com.uniquindio.CINEMAX.negocio.Service.WompiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
/**
 * Implementación de la interfaz WompiService para gestionar operaciones auxiliares del proveedor Wompi.
 * Esta clase genera firmas de integridad, construye los datos requeridos por el checkout y extrae datos básicos
 * de los eventos webhook recibidos desde el proveedor de pagos.
 */
@Service
@RequiredArgsConstructor
public class WompiServiceImpl implements WompiService {

    private final WompiProperties wompiProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Genera la firma de integridad requerida para iniciar el checkout de Wompi.
     * La firma se construye a partir de la referencia, el monto en centavos, la moneda y el secreto de integridad.
     */
    @Override
    public String generarFirmaIntegridad(String referencia, Long montoCentavos, String moneda) {
        try {
            String raw = referencia + montoCentavos + moneda + wompiProperties.getIntegritySecret();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar la firma de integridad para Wompi", e);
        }
    }

    /**
     * Construye los datos necesarios para que el frontend pueda abrir el checkout de Wompi.
     */
    @Override
    public WompiCheckoutDataDTO construirCheckout(PagoEntity pago) {
        String signature = generarFirmaIntegridad(
                pago.getReferencia(),
                pago.getMontoCentavos(),
                pago.getMoneda()
        );

        return new WompiCheckoutDataDTO(
                wompiProperties.getPublicKey(),
                pago.getReferencia(),
                pago.getMontoCentavos(),
                pago.getMoneda(),
                signature,
                wompiProperties.getRedirectUrl(),
                wompiProperties.getEnvironment()
        );
    }

    /**
     * Extrae la referencia enviada por Wompi dentro del payload del webhook.
     */
    @Override
    @SuppressWarnings("unchecked")
    public String extraerReferencia(WompiWebhookDTO webhook) {
        Map<String, Object> transaction = extraerTransactionMap(webhook);
        Object reference = transaction.get("reference");
        return reference == null ? null : String.valueOf(reference);
    }

    /**
     * Extrae el identificador de transacción enviado por Wompi dentro del payload del webhook.
     */
    @Override
    @SuppressWarnings("unchecked")
    public String extraerTransactionId(WompiWebhookDTO webhook) {
        Map<String, Object> transaction = extraerTransactionMap(webhook);
        Object id = transaction.get("id");
        return id == null ? null : String.valueOf(id);
    }

    /**
     * Extrae el estado de la transacción enviado por Wompi dentro del payload del webhook.
     */
    @Override
    @SuppressWarnings("unchecked")
    public String extraerEstadoTransaccion(WompiWebhookDTO webhook) {
        Map<String, Object> transaction = extraerTransactionMap(webhook);
        Object status = transaction.get("status");
        return status == null ? null : String.valueOf(status);
    }

    /**
     * Convierte el webhook completo en JSON para almacenarlo como bitácora de auditoría.
     */
    @Override
    public String payloadToJson(WompiWebhookDTO webhook) {
        try {
            return objectMapper.writeValueAsString(webhook);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extraerTransactionMap(WompiWebhookDTO webhook) {
        if (webhook == null || webhook.data() == null) {
            return Map.of();
        }

        Object transactionObj = webhook.data().get("transaction");

        if (transactionObj instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }

        return Map.of();
    }
}