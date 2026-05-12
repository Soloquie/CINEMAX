package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.CompraResumenDTO;
import com.uniquindio.CINEMAX.negocio.DTO.VentaResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.ComprobanteVentaService;
import com.uniquindio.CINEMAX.negocio.Service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para consultar ventas confirmadas en el sistema CINEMAX.
 * Permite al usuario autenticado consultar el detalle de una venta propia.
 */
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor

public class VentaController {
    private final ComprobanteVentaService comprobanteVentaService;

    private final VentaService ventaService;

    /**
     * Endpoint para consultar el detalle de una venta confirmada.
     */
    @GetMapping("/{id}")
    public VentaResponseDTO detalleVenta(
            @PathVariable Long id,
            Authentication auth
    ) {
        return ventaService.detalleVenta(id, auth.getName());
    }

    @GetMapping("/{ventaId}/comprobante/pdf")
    public ResponseEntity<byte[]> descargarComprobantePdf(
            @PathVariable Long ventaId,
            Authentication auth
    ) {
        byte[] pdf = comprobanteVentaService.generarPdfComprobante(ventaId, auth.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=comprobante-cinemax-" + ventaId + ".pdf"
                )
                .body(pdf);
    }

    @PostMapping("/{ventaId}/comprobante/email")
    public ResponseEntity<Map<String, String>> enviarComprobanteCorreo(
            @PathVariable Long ventaId,
            Authentication auth
    ) {
        comprobanteVentaService.enviarComprobantePorCorreo(ventaId, auth.getName());

        return ResponseEntity.ok(
                Map.of("mensaje", "Comprobante enviado correctamente al correo.")
        );
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<List<CompraResumenDTO>> listarMisCompras(Authentication auth) {
        return ResponseEntity.ok(ventaService.listarMisCompras(auth.getName()));
    }
}