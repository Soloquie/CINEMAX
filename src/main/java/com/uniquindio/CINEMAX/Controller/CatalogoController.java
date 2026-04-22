package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para la gestión del catálogo de cines y películas en el sistema CINEMAX.
 * Este controlador proporciona endpoints para listar cines, salas, películas, géneros y detalles de películas.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;
    /**
     * Endpoint para listar los cines disponibles en el sistema, con opción de filtrado por ciudad y paginación.
     * @param ciudad Parámetro de filtro opcional para listar solo los cines ubicados en una ciudad específica.
     * @param page Número de página para la paginación (por defecto es 0).
     * @param size Tamaño de página para la paginación (por defecto es 10).
     * @return Un objeto Page que contiene una lista de objetos CinePublicDTO con la información de los cines disponibles,
     * filtrados por ciudad si se proporciona el parámetro, junto con información de paginación.
     */
    @GetMapping("/cines")
    public Page<CinePublicDTO> listarCines(
            @RequestParam(required = false) String ciudad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return catalogoService.listarCines(ciudad, page, size);
    }
    /**
     * Endpoint para listar las salas de un cine específico.
     * @param cineId El ID del cine para el cual se desean listar las salas.
     * @return Una lista de objetos SalaPublicDTO que contienen la información de las salas disponibles en el cine especificado.
     */
    @GetMapping("/cines/{cineId}/salas")
    public List<SalaPublicDTO> listarSalas(@PathVariable Long cineId) {
        return catalogoService.listarSalasPorCine(cineId);
    }
    /**
     * Endpoint para listar las películas disponibles en el sistema con opciones de filtrado y paginación.
     * @param q Parámetro de búsqueda opcional para filtrar películas por título o sinopsis.
     * @param generoId Parámetro de filtro opcional para filtrar películas por género específico.
     * @param desde Parámetro de filtro opcional para filtrar películas estrenadas a partir de una fecha específica (formato yyyy-MM-dd).
     * @param hasta Parámetro de filtro opcional para filtrar películas estrenadas hasta una fecha específica (formato yyyy-MM-dd).
     * @param page Número de página para la paginación (por defecto es 0).
     * @param size Tamaño de página para la paginación (por defecto es 12).
     * @return Un objeto Page que contiene una lista de PeliculaCardDTO con la información de las películas que cumplen con los criterios de búsqueda y filtrado, junto con información de paginación.
     */
    @GetMapping("/peliculas")
    public Page<PeliculaCardDTO> listarPeliculas(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long generoId,
            @RequestParam(required = false) String desde, // yyyy-MM-dd
            @RequestParam(required = false) String hasta, // yyyy-MM-dd
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return catalogoService.listarPeliculas(q, generoId, desde, hasta, page, size);
    }
    /**
     * Endpoint para obtener los detalles de una película específica.
     * @param peliculaId El ID de la película para la cual se desean obtener los detalles.
     * @return Un objeto PeliculaDetailDTO que contiene la información detallada de la película, incluyendo su título, sinopsis, duración, género, elenco, etc.
     */
    @GetMapping("/peliculas/{peliculaId}")
    public PeliculaDetailDTO detalle(@PathVariable Long peliculaId) {
        return catalogoService.detallePelicula(peliculaId);
    }
    /**
     * Endpoint para listar las películas que se estrenarán próximamente en los próximos días.
     * @param dias El número de días a partir de hoy para considerar una película como "próxima". Por defecto es 30 días.
     * @return Una lista de objetos PeliculaCardDTO que contienen la información de las películas que se estrenarán próximamente dentro del rango de días especificado.
     */
    @GetMapping("/peliculas/proximas")
    public List<PeliculaCardDTO> proximas(@RequestParam(defaultValue = "30") int dias) {
        return catalogoService.proximas(dias);
    }
    /**
     * Endpoint para listar las películas que actualmente están en cartelera.
     * @return Una lista de objetos PeliculaCardDTO que contienen la información de las películas que están en cartelera.
     */
    @GetMapping("/peliculas/en-cartelera")
    public List<PeliculaCardDTO> enCartelera() {
        return catalogoService.enCartelera();
    }
    /**
     * Endpoint para listar todos los géneros de películas disponibles en el sistema.
     * @return Una lista de objetos GeneroDTO que contienen la información de todos los géneros de películas en el sistema.
     */
    @GetMapping("/generos")
    public List<GeneroDTO> listarGeneros() {
        return catalogoService.listarGeneros();
    }
}