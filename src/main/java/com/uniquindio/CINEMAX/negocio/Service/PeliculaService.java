package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.PeliculaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PeliculaUpsertFormDTO;

import java.util.List;
/**
 * Interfaz de servicio para gestionar las películas en el sistema CINEMAX.
 * Esta interfaz define los métodos necesarios para crear, actualizar, eliminar y listar películas.
 */
public interface PeliculaService {
    PeliculaResponseDTO crear(PeliculaUpsertFormDTO form);
    PeliculaResponseDTO actualizar(Long id, PeliculaUpsertFormDTO form);
    void eliminar(Long id);
    List<PeliculaResponseDTO> listarTodasAdmin();
    List<PeliculaResponseDTO> listarActivas(); // para cartelera cliente luego
}