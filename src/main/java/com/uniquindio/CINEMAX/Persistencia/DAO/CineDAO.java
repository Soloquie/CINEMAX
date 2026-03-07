package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.DTO.CineResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.CineUpsertDTO;

import java.util.List;

public interface CineDAO {
    CineResponseDTO crear(CineUpsertDTO dto);
    CineResponseDTO actualizar(Long id, CineUpsertDTO dto);
    void eliminar(Long id); // soft: activo=false
    List<CineResponseDTO> listar();
}