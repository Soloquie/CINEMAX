package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import org.springframework.data.domain.Page;

import java.util.List;
/* Interfaz de servicio para gestionar el catálogo de cines, salas, películas y géneros en el sistema CINEMAX.
 * Define las operaciones disponibles para listar cines, salas, películas con filtros y paginación, obtener detalles
 * de películas específicas, listar próximas películas, películas en cartelera y géneros disponibles.
 */
public interface CatalogoService {
    Page<CinePublicDTO> listarCines(String ciudad, int page, int size);
    List<SalaPublicDTO> listarSalasPorCine(Long cineId);
    Page<PeliculaCardDTO> listarPeliculas(String q, Long generoId, String desde, String hasta, int page, int size);
    PeliculaDetailDTO detallePelicula(Long peliculaId);
    List<PeliculaCardDTO> proximas(int dias);
    List<PeliculaCardDTO> enCartelera();
    List<GeneroDTO> listarGeneros();
}