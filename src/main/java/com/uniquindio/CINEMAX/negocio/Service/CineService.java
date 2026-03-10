package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.CineResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.CineUpsertDTO;

import java.util.List;
/**
 * Interfaz de servicio para gestionar los cines en el sistema CINEMAX.
 * Esta interfaz define los métodos necesarios para crear, actualizar, eliminar y listar cines.
 */
public interface CineService {
    CineResponseDTO crear(CineUpsertDTO dto);
    CineResponseDTO actualizar(Long id, CineUpsertDTO dto);
    void eliminar(Long id);
    List<CineResponseDTO> listar();
}