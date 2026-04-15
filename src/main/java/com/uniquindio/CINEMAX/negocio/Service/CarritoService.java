package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.CarritoResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Interfaz de servicio para gestionar el carrito de compras en el sistema CINEMAX.
 * Esta interfaz define los métodos necesarios para agregar asientos en espera al carrito, eliminar asientos del carrito
 * y obtener el contenido del carrito de un usuario específico.
 */
public interface CarritoService {
    void addSeatHoldsToCart(String userEmail, List<Long> funcionAsientoIds, LocalDateTime expiraEn);
    void removeSeatHoldsFromCart(String userEmail, List<Long> funcionAsientoIds);
    CarritoResponseDTO getMyCart(String userEmail);

}