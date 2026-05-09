package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.MercadoPagoCheckoutResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PagoEstadoResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para gestionar el flujo de pagos en el sistema CINEMAX.
 * Proporciona endpoints para crear checkout, consultar estado de pago y recibir webhooks del proveedor externo.
 */
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    /**
     * Endpoint para crear un checkout de pago a partir del carrito activo del usuario autenticado.
     */
    @PostMapping("/checkout")
    public MercadoPagoCheckoutResponseDTO crearCheckout(Authentication auth) {
        return pagoService.crearCheckout(auth.getName());
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
}