package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.VentaResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
/**
 * Controlador REST para consultar ventas confirmadas en el sistema CINEMAX.
 * Permite al usuario autenticado consultar el detalle de una venta propia.
 */
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

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
}