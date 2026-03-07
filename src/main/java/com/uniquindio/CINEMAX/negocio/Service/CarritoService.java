package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.CarritoResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CarritoService {
    void addSeatHoldsToCart(String userEmail, List<Long> funcionAsientoIds, LocalDateTime expiraEn);
    void removeSeatHoldsFromCart(String userEmail, List<Long> funcionAsientoIds);
    CarritoResponseDTO getMyCart(String userEmail);
}