package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.AsientoDAO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.AsientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Implementación de la interfaz AsientoService para gestionar los asientos en el sistema CINEMAX.
 * Esta clase utiliza un DAO para interactuar con la base de datos y realizar las operaciones necesarias.
 */
@Service
@RequiredArgsConstructor
public class AsientoServiceImpl implements AsientoService {

    private final AsientoDAO asientoDAO;
    /* Implementación del método para generar asientos en una sala específica. */
    @Override public AsientosGenerarResponseDTO generar(Long salaId, AsientosGenerarDTO dto) { return asientoDAO.generar(salaId, dto); }
    @Override public List<AsientoResponseDTO> listarPorSala(Long salaId) { return asientoDAO.listarPorSala(salaId); }
}