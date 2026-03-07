package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.*;

import java.util.List;

public interface SeleccionAsientosService {
    List<FuncionAsientoResponseDTO> listarAsientos(Long funcionId, String emailUsuario);
    HoldAsientosResponseDTO hold(Long funcionId, String emailUsuario, HoldAsientosRequestDTO request);
    MessageResponseDTO release(Long funcionId, String emailUsuario, ReleaseAsientosRequestDTO request);
}