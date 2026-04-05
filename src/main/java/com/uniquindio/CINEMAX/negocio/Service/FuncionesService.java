package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.FuncionPublicDTO;

import java.time.LocalDate;
import java.util.List;
/**
 * Interfaz de servicio para gestionar las funciones en el sistema CINEMAX.
 * Esta interfaz define los métodos necesarios para listar las funciones disponibles según diferentes criterios, como fecha, cine y película.
 */
public interface FuncionesService {
    List<FuncionPublicDTO> listar(LocalDate fecha, Long cineId, Long peliculaId);
    List<FuncionPublicDTO> listarPorCine(Long cineId, LocalDate fecha);
}