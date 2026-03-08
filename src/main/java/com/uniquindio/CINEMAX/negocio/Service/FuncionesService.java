package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.FuncionPublicDTO;

import java.time.LocalDate;
import java.util.List;

public interface FuncionesService {
    List<FuncionPublicDTO> listar(LocalDate fecha, Long cineId, Long peliculaId);
    List<FuncionPublicDTO> listarPorCine(Long cineId, LocalDate fecha);
}