package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.DTO.AsientoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarResponseDTO;

import java.util.List;
/* Interfaz DAO para gestionar las operaciones relacionadas con los asientos en el sistema CINEMAX. */
public interface AsientoDAO {
    AsientosGenerarResponseDTO generar(Long salaId, AsientosGenerarDTO dto);
    List<AsientoResponseDTO> listarPorSala(Long salaId);
}