package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.CineDAO;
import com.uniquindio.CINEMAX.negocio.DTO.CineResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.CineUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.CineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Implementación de la interfaz CineService para gestionar los cines en el sistema CINEMAX.
 * Esta clase utiliza un DAO para interactuar con la base de datos y realizar las operaciones necesarias.
 */
@Service
@RequiredArgsConstructor
public class CineServiceImpl implements CineService {

    private final CineDAO cineDAO;
    /* Implementación del método para crear un nuevo cine. */
    @Override public CineResponseDTO crear(CineUpsertDTO dto) { return cineDAO.crear(dto); }
    @Override public CineResponseDTO actualizar(Long id, CineUpsertDTO dto) { return cineDAO.actualizar(id, dto); }
    @Override public void eliminar(Long id) { cineDAO.eliminar(id); }
    @Override public List<CineResponseDTO> listar() { return cineDAO.listar(); }
}