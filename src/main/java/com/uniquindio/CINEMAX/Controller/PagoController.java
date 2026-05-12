package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.CheckoutPagoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.CrearCheckoutPagoRequestDTO;
import com.uniquindio.CINEMAX.negocio.DTO.MercadoPagoCheckoutResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PagoEstadoResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Controlador REST para gestionar el flujo de pagos en el sistema CINEMAX.
 * Proporciona endpoints para crear checkout, consultar estado de pago y recibir webhooks del proveedor externo.
 */
@Slf4j
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    /**
     * Endpoint para crear un checkout de pago a partir del carrito activo del usuario autenticado.
     */
    @PostMapping("/checkout")
    public CheckoutPagoResponseDTO crearCheckout(
            @RequestBody(required = false) CrearCheckoutPagoRequestDTO request,
            Authentication auth
    ) {
        return pagoService.crearCheckout(auth.getName(), request);
    }

    /**
     * Endpoint para consultar el estado actual de un pago por referencia.
     */
    @GetMapping("/estado/{referencia}")
    public PagoEstadoResponseDTO consultarEstado(
            @PathVariable String referencia,
            Authentication auth
    ) {
        return pagoService.consultarEstado(referencia, auth.getName());
    }

    /**
     * Endpoint público para recibir eventos webhook enviados por Mercado Pago.
     * Mercado Pago puede enviar información tanto en el body como en parámetros de la URL.
     */
    @PostMapping("/webhook/mercado-pago")
    public void webhookMercadoPago(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam Map<String, String> params
    ) {
        pagoService.procesarWebhookMercadoPago(body, params);
    }

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> webhookStripe(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature
    ) {
        try {
            pagoService.procesarWebhookStripe(payload, signature);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            log.error("Error procesando webhook de Stripe", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando webhook de Stripe: " + e.getMessage());
        }
    }
}