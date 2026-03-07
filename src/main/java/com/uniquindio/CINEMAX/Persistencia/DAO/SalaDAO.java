package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.DTO.SalaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.SalaUpsertDTO;

import java.util.List;

public interface SalaDAO {
    SalaResponseDTO crear(Long cineId, SalaUpsertDTO dto);
    SalaResponseDTO actualizar(Long salaId, SalaUpsertDTO dto);
    void eliminar(Long salaId); // soft: activa=false
    List<SalaResponseDTO> listarPorCine(Long cineId);
}