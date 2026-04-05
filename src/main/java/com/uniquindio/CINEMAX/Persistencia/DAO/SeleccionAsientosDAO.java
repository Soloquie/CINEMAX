package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.DTO.*;

import java.util.List;
/* Interfaz DAO para gestionar las operaciones relacionadas con la selección de asientos en el sistema CINEMAX. */
public interface SeleccionAsientosDAO {
    List<FuncionAsientoResponseDTO> listarAsientos(Long funcionId, String emailUsuario);
    HoldAsientosResponseDTO hold(Long funcionId, String emailUsuario, HoldAsientosRequestDTO request);
    MessageResponseDTO release(Long funcionId, String emailUsuario, ReleaseAsientosRequestDTO request);
}