package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.FuncionDAO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.FuncionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Implementación de la interfaz FuncionService para gestionar las funciones en el sistema CINEMAX.
 * Esta clase utiliza un DAO para interactuar con la base de datos y realizar las operaciones necesarias.
 */
@Service
@RequiredArgsConstructor
public class FuncionServiceImpl implements FuncionService {
    /* Implementación del método para crear una nueva función. */
    private final FuncionDAO funcionDAO;
    @Override public FuncionResponseDTO crear(FuncionUpsertDTO dto) { return funcionDAO.crear(dto); }
    /* Implementación del método para actualizar una función existente. */
    @Override public FuncionResponseDTO actualizar(Long id, FuncionUpsertDTO dto) { return funcionDAO.actualizar(id, dto); }
    /* Implementación del método para cancelar una función por su ID. */
    @Override public FuncionResponseDTO cancelar(Long id) { return funcionDAO.cancelar(id); }
    /* Implementación del método para listar todas las funciones disponibles para los usuarios. */
    @Override public List<FuncionResponseDTO> listarTodasAdmin() { return funcionDAO.listarTodas(); }
    /* Implementación del método para listar todas las funciones programadas para los usuarios. */
    @Override public List<FuncionResponseDTO> listarProgramadasPorPelicula(Long peliculaId) { return funcionDAO.listarProgramadasPorPelicula(peliculaId); }
}