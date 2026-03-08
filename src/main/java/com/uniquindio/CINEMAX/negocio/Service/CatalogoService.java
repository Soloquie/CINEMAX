package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CatalogoService {
    List<CinePublicDTO> listarCines(String ciudad);
    List<SalaPublicDTO> listarSalasPorCine(Long cineId);

    Page<PeliculaCardDTO> listarPeliculas(String q, Long generoId, String desde, String hasta, int page, int size);
    PeliculaDetailDTO detallePelicula(Long peliculaId);

    List<PeliculaCardDTO> proximas(int dias);
    List<PeliculaCardDTO> enCartelera();

    List<GeneroDTO> listarGeneros();
}