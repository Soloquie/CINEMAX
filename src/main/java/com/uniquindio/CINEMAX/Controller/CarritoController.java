package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.CarritoResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public CarritoResponseDTO miCarrito(Authentication auth) {
        return carritoService.getMyCart(auth.getName());
    }
}