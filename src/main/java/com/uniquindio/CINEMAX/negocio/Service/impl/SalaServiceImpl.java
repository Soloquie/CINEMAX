package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.SalaDAO;
import com.uniquindio.CINEMAX.negocio.DTO.SalaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.SalaUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Implementación de la interfaz SalaService para gestionar las salas en el sistema CINEMAX.
 * Esta clase utiliza un DAO para interactuar con la base de datos y realizar las operaciones necesarias.
 */
@Service
@RequiredArgsConstructor
public class SalaServiceImpl implements SalaService {

    private final SalaDAO salaDAO;
    /* Implementación del método para crear una nueva sala. */
    @Override public SalaResponseDTO crear(Long cineId, SalaUpsertDTO dto) { return salaDAO.crear(cineId, dto); }
    /* Implementación del método para actualizar una sala existente. */
    @Override public SalaResponseDTO actualizar(Long salaId, SalaUpsertDTO dto) { return salaDAO.actualizar(salaId, dto); }
    /* Implementación del método para eliminar una sala por su ID. */
    @Override public void eliminar(Long salaId) { salaDAO.eliminar(salaId); }
    /* Implementación del método para listar todas las salas disponibles. */
    @Override public List<SalaResponseDTO> listarPorCine(Long cineId) { return salaDAO.listarPorCine(cineId); }
}