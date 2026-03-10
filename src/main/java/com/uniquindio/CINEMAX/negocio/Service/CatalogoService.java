package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import org.springframework.data.domain.Page;

import java.util.List;
/**
 * Interfaz de servicio para gestionar el catálogo de cines, salas y películas en el sistema CINEMAX.
 * Esta interfaz define los métodos necesarios para listar cines por ciudad, listar salas por cine,
 * listar películas con filtros de búsqueda, obtener detalles de una película específica, listar próximas películas
 * y películas en cartelera, así como listar los géneros disponibles.
 */
public interface CatalogoService {
    List<CinePublicDTO> listarCines(String ciudad);
    List<SalaPublicDTO> listarSalasPorCine(Long cineId);

    Page<PeliculaCardDTO> listarPeliculas(String q, Long generoId, String desde, String hasta, int page, int size);
    PeliculaDetailDTO detallePelicula(Long peliculaId);

    List<PeliculaCardDTO> proximas(int dias);
    List<PeliculaCardDTO> enCartelera();

    List<GeneroDTO> listarGeneros();
}