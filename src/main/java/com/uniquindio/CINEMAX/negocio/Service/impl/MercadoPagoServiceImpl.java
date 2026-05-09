package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.uniquindio.CINEMAX.config.MercadoPagoProperties;
import com.uniquindio.CINEMAX.Persistencia.Entity.PagoEntity;
import com.uniquindio.CINEMAX.negocio.DTO.MercadoPagoPreferenceDataDTO;
import com.uniquindio.CINEMAX.negocio.Exception.PagoInvalidoException;
import com.uniquindio.CINEMAX.negocio.Service.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

import java.util.Map;
/**
 * Implementación de MercadoPagoService para crear preferencias de Checkout Pro
 * y consultar pagos aprobados desde Mercado Pago.
 */
@Service
@RequiredArgsConstructor
public class MercadoPagoServiceImpl implements MercadoPagoService {

    private final MercadoPagoProperties mercadoPagoProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Crea una preferencia de pago en Mercado Pago a partir del pago pendiente generado por CINEMAX.
     */
    @Override
    public MercadoPagoPreferenceDataDTO crearPreferencia(PagoEntity pago) {
        try {
            MercadoPagoConfig.setAccessToken(getAccessTokenSeguro());

            String successUrl = construirUrlRetorno(mercadoPagoProperties.getSuccessUrl(), pago.getReferencia());
            String failureUrl = construirUrlRetorno(mercadoPagoProperties.getFailureUrl(), pago.getReferencia());
            String pendingUrl = construirUrlRetorno(mercadoPagoProperties.getPendingUrl(), pago.getReferencia());

            System.out.println("MP successUrl = " + successUrl);
            System.out.println("MP failureUrl = " + failureUrl);
            System.out.println("MP pendingUrl = " + pendingUrl);

            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title("Compra CINEMAX")
                    .description("Boletas y productos de confitería")
                    .quantity(1)
                    .currencyId(mercadoPagoProperties.getCurrency())
                    .unitPrice(pago.getMonto())
                    .build();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(successUrl)
                    .failure(failureUrl)
                    .pending(pendingUrl)
                    .build();

            PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                    .items(java.util.List.of(item))
                    .backUrls(backUrls)
                    .externalReference(pago.getReferencia());

            String notificationUrl = mercadoPagoProperties.getNotificationUrl();

            if (notificationUrl != null
                    && !notificationUrl.isBlank()
                    && notificationUrl.startsWith("https://")
                    && !notificationUrl.contains("localhost")) {
                builder.notificationUrl(notificationUrl);
            }

            PreferenceRequest request = builder.build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(request);

            return new MercadoPagoPreferenceDataDTO(
                    preference.getId(),
                    preference.getInitPoint(),
                    preference.getSandboxInitPoint()
            );

        } catch (MPApiException e) {
            String detalle = e.getApiResponse() != null
                    ? e.getApiResponse().getContent()
                    : e.getMessage();

            System.err.println("ERROR MERCADO PAGO STATUS: " +
                    (e.getApiResponse() != null ? e.getApiResponse().getStatusCode() : "SIN_STATUS"));

            System.err.println("ERROR MERCADO PAGO BODY: " + detalle);

            throw new PagoInvalidoException("No se pudo crear la preferencia de Mercado Pago: " + detalle);

        } catch (MPException e) {
            System.err.println("ERROR MERCADO PAGO SDK: " + e.getMessage());
            throw new PagoInvalidoException("No se pudo crear la preferencia de Mercado Pago: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("ERROR GENERAL MERCADO PAGO: " + e.getMessage());
            throw new PagoInvalidoException("No se pudo crear la preferencia de Mercado Pago: " + e.getMessage());
        }
    }

    /**
     * Extrae el ID del pago desde el webhook. Se soportan tanto datos en body como parámetros.
     */
    @Override
    @SuppressWarnings("unchecked")
    public String extraerPaymentId(Map<String, Object> body, Map<String, String> params) {
        if (body != null) {
            Object dataObj = body.get("data");

            if (dataObj instanceof Map<?, ?> map) {
                Object id = ((Map<String, Object>) map).get("id");
                if (id != null) return String.valueOf(id);
            }

            Object id = body.get("id");
            if (id != null) return String.valueOf(id);
        }

        if (params != null) {
            if (params.get("data.id") != null) return params.get("data.id");
            if (params.get("id") != null) return params.get("id");
        }

        return null;
    }

    /**
     * Consulta en Mercado Pago el estado real del pago recibido por webhook.
     */
    @Override
    public Payment obtenerPago(String paymentId) {
        try {
            MercadoPagoConfig.setAccessToken(getAccessTokenSeguro());
            PaymentClient client = new PaymentClient();
            return client.get(Long.valueOf(paymentId));

        } catch (Exception e) {
            throw new PagoInvalidoException("No se pudo consultar el pago en Mercado Pago: " + e.getMessage());
        }
    }

    /**
     * Convierte el webhook y parámetros recibidos a JSON para auditoría.
     */
    @Override
    public String payloadToJson(Map<String, Object> body, Map<String, String> params) {
        try {
            return objectMapper.writeValueAsString(
                    Map.of(
                            "body", body == null ? Map.of() : body,
                            "params", params == null ? Map.of() : params
                    )
            );
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private String construirUrlRetorno(String baseUrl, String referencia) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new PagoInvalidoException(
                    "No se configuraron correctamente las URLs de retorno de Mercado Pago. Revisa success-url, failure-url y pending-url."
            );
        }

        String separador = baseUrl.contains("?") ? "&" : "?";
        return baseUrl.trim() + separador + "referencia=" + referencia;
    }

    private String getAccessTokenSeguro() {
        String token = mercadoPagoProperties.getAccessToken();

        if (token == null || token.isBlank() || token.equals("TEST-access-token")) {
            throw new PagoInvalidoException(
                    "No se configuró correctamente el Access Token de Mercado Pago. Revisa application-dev.properties o las variables de entorno."
            );
        }

        return token.trim();
    }
}