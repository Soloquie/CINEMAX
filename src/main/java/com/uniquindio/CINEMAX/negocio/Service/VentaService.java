package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.VentaResponseDTO;
/* Interfaz de servicio para gestionar ventas confirmadas en el sistema CINEMAX.
 * Define operaciones para confirmar una venta desde un pago aprobado y consultar detalles de ventas.
 */
public interface VentaService {

    VentaResponseDTO confirmarVentaDesdePago(String referencia);

    VentaResponseDTO detalleVenta(Long ventaId, String userEmail);
}