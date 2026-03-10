package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionUpsertDTO;

import java.util.List;
/* Interfaz DAO para gestionar las operaciones relacionadas con las funciones en el sistema CINEMAX. */
public interface FuncionDAO {
    FuncionResponseDTO crear(FuncionUpsertDTO dto);
    FuncionResponseDTO actualizar(Long id, FuncionUpsertDTO dto);
    FuncionResponseDTO cancelar(Long id); // soft: estado=CANCELADA
    List<FuncionResponseDTO> listarTodas();
    List<FuncionResponseDTO> listarProgramadasPorPelicula(Long peliculaId);
}