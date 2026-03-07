package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.SalaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.SalaUpsertDTO;

import java.util.List;

public interface SalaService {
    SalaResponseDTO crear(Long cineId, SalaUpsertDTO dto);
    SalaResponseDTO actualizar(Long salaId, SalaUpsertDTO dto);
    void eliminar(Long salaId);
    List<SalaResponseDTO> listarPorCine(Long cineId);
}