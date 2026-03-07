package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.DTO.*;

import java.util.List;

public interface SeleccionAsientosDAO {
    List<FuncionAsientoResponseDTO> listarAsientos(Long funcionId, String emailUsuario);
    HoldAsientosResponseDTO hold(Long funcionId, String emailUsuario, HoldAsientosRequestDTO request);
    MessageResponseDTO release(Long funcionId, String emailUsuario, ReleaseAsientosRequestDTO request);
}