package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionUpsertDTO;

import java.util.List;
/**
 * Interfaz de servicio para gestionar las funciones en el sistema CINEMAX.
 * Esta interfaz define los métodos necesarios para crear, actualizar, cancelar y listar funciones.
 */
public interface FuncionService {
    FuncionResponseDTO crear(FuncionUpsertDTO dto);
    FuncionResponseDTO actualizar(Long id, FuncionUpsertDTO dto);
    FuncionResponseDTO cancelar(Long id);
    List<FuncionResponseDTO> listarTodasAdmin();
    List<FuncionResponseDTO> listarProgramadasPorPelicula(Long peliculaId);
}