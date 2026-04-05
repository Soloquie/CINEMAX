package com.uniquindio.CINEMAX.negocio.DTO;

import java.time.LocalDateTime;
import java.util.List;
/**
 * DTO (Data Transfer Object) para representar la información del carrito de compras en el sistema CINEMAX.
 * Este DTO se utiliza para enviar la información del carrito al cliente, incluyendo su ID, estado,
 * fecha de expiración y la lista de ítems que contiene.
 */
public record CarritoResponseDTO(
        Long carritoId,
        String estado,
        LocalDateTime expiraEn,
        List<CarritoItemResponseDTO> items
) {}