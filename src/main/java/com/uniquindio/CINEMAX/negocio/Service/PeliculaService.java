package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.PeliculaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PeliculaUpsertFormDTO;

import java.util.List;

public interface PeliculaService {
    PeliculaResponseDTO crear(PeliculaUpsertFormDTO form);
    PeliculaResponseDTO actualizar(Long id, PeliculaUpsertFormDTO form);
    void eliminar(Long id);
    List<PeliculaResponseDTO> listarTodasAdmin();
    List<PeliculaResponseDTO> listarActivas(); // para cartelera cliente luego
}