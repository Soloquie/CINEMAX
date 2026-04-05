package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.*;

import java.util.List;
/**
 * Interfaz de servicio para gestionar la selección de asientos en el sistema CINEMAX.
 * Esta interfaz define los métodos necesarios para listar asientos disponibles, reservar (hold) y liberar asientos.
 */
public interface SeleccionAsientosService {
    List<FuncionAsientoResponseDTO> listarAsientos(Long funcionId, String emailUsuario);
    HoldAsientosResponseDTO hold(Long funcionId, String emailUsuario, HoldAsientosRequestDTO request);
    MessageResponseDTO release(Long funcionId, String emailUsuario, ReleaseAsientosRequestDTO request);
}