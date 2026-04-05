package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.AsientoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarResponseDTO;

import java.util.List;
/**
 * Interfaz de servicio para gestionar los asientos en el sistema CINEMAX.
 * Esta interfaz define los métodos necesarios para generar asientos en una sala y listar los asientos disponibles por sala.
 */
public interface AsientoService {
    AsientosGenerarResponseDTO generar(Long salaId, AsientosGenerarDTO dto);
    List<AsientoResponseDTO> listarPorSala(Long salaId);
}