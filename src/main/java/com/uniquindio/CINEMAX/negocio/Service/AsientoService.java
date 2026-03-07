package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.AsientoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarResponseDTO;

import java.util.List;

public interface AsientoService {
    AsientosGenerarResponseDTO generar(Long salaId, AsientosGenerarDTO dto);
    List<AsientoResponseDTO> listarPorSala(Long salaId);
}