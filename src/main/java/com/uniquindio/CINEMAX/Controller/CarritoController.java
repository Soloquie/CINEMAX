package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
/**
 * Controlador REST para la gestión del carrito de compras en el sistema CINEMAX.
 * Este controlador proporciona un endpoint para que los usuarios puedan ver su carrito de compras actual.
 */
@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {
    // Inyección del servicio de carrito para manejar la lógica de negocio relacionada con el carrito de compras.
    private final CarritoService carritoService;
    /**
     * Endpoint para obtener el carrito de compras del usuario autenticado.
     * @param auth El objeto Authentication que contiene la información del usuario autenticado, incluyendo su nombre de usuario.
     * @return Un objeto CarritoResponseDTO que contiene la información del carrito de compras del usuario, incluyendo los items agregados, el total a pagar, etc.
     */
    @GetMapping
    public CarritoResponseDTO miCarrito(Authentication auth) {
        return carritoService.getMyCart(auth.getName());
    }

    @PostMapping("/remove-seats")
    public MessageResponseDTO removeSeats(
            @Valid @RequestBody RemoveCartItemsRequestDTO request,
            Authentication auth
    ) {
        carritoService.removeSeatHoldsFromCart(auth.getName(), request.funcionAsientoIds());
        return new MessageResponseDTO("Ítems eliminados del carrito correctamente.");
    }

    @PostMapping("/productos")
    public MessageResponseDTO addProducto(
            @Valid @RequestBody AgregarProductoCarritoDTO request,
            Authentication auth
    ) {
        carritoService.addProductsToCart(auth.getName(), request.productoId(), request.cantidad());
        return new MessageResponseDTO("Producto agregado al carrito correctamente.");
    }

    @PutMapping("/productos/{productoId}")
    public MessageResponseDTO updateProducto(
            @PathVariable Long productoId,
            @Valid @RequestBody ActualizarProductoCarritoDTO request,
            Authentication auth
    ) {
        carritoService.updateProductQuantity(auth.getName(), productoId, request.cantidad());
        return new MessageResponseDTO("Cantidad actualizada correctamente.");
    }

    @DeleteMapping("/productos/{productoId}")
    public MessageResponseDTO removeProducto(
            @PathVariable Long productoId,
            Authentication auth
    ) {
        carritoService.removeProductFromCart(auth.getName(), productoId);
        return new MessageResponseDTO("Producto eliminado del carrito.");
    }
}