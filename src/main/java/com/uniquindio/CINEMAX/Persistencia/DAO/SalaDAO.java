package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.DTO.SalaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.SalaUpsertDTO;

import java.util.List;
/* Interfaz DAO para gestionar las operaciones relacionadas con las salas en el sistema CINEMAX. */
public interface SalaDAO {
    SalaResponseDTO crear(Long cineId, SalaUpsertDTO dto);
    SalaResponseDTO actualizar(Long salaId, SalaUpsertDTO dto);
    void eliminar(Long salaId); // soft: activa=false
    List<SalaResponseDTO> listarPorCine(Long cineId);
}