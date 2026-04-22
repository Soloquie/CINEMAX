package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.Repository.*;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Exception.CineInactivoException;
import com.uniquindio.CINEMAX.negocio.Exception.CineNoEncontradoException;
import com.uniquindio.CINEMAX.negocio.Exception.FiltroInvalidoException;
import com.uniquindio.CINEMAX.negocio.Exception.PeliculaNoEncontradaException;
import com.uniquindio.CINEMAX.negocio.Service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uniquindio.CINEMAX.Persistencia.Entity.CineEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * Implementación de la interfaz CatalogoService para gestionar el catálogo de cines, salas, películas y funciones en el sistema CINEMAX.
 * Esta clase utiliza varios repositorios para interactuar con la base de datos y realizar las operaciones necesarias.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoServiceImpl implements CatalogoService {

    private final CineRepository cineRepository;
    private final SalaRepository salaRepository;
    private final PeliculaRepository peliculaRepository;
    private final FuncionRepository funcionRepository;
    private final GeneroRepository generoRepository;

    /**
     * Lista los cines activos, con opción de filtrar por ciudad. El resultado se ordena por ciudad y nombre.
     * @param ciudad filtro opcional para buscar cines por ciudad (mínimo 2 caracteres). Si es nulo o vacío,
     * se listan todos los cines activos.
     * @param page número de página para la paginación (0-based). Debe ser un número entero no negativo.
     * @param size tamaño de página para la paginación. Debe ser un número entero entre 1 y 50.
     * @return Página de cines que cumplen con el filtro, cada uno representado como un CinePublicDTO.
     * @throws FiltroInvalidoException si los parámetros de paginación son inválidos o si el filtro de ciudad es demasiado corto.
     */
    @Override
    public Page<CinePublicDTO> listarCines(String ciudad, int page, int size) {
        if (page < 0) throw new FiltroInvalidoException("page no puede ser negativo.");
        if (size < 1 || size > 50) throw new FiltroInvalidoException("size debe estar entre 1 y 50.");
        if (ciudad != null && !ciudad.isBlank() && ciudad.trim().length() < 2) {
            throw new FiltroInvalidoException("El filtro ciudad debe tener al menos 2 caracteres.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("ciudad").ascending().and(Sort.by("nombre").ascending()));

        Page<CineEntity> cines = (ciudad == null || ciudad.isBlank())
                ? cineRepository.findByActivoTrue(pageable)
                : cineRepository.findByActivoTrueAndCiudadContainingIgnoreCase(ciudad.trim(), pageable);

        return cines.map(c -> new CinePublicDTO(c.getId(), c.getNombre(), c.getCiudad(), c.getDireccion()));
    }
    /**
     * Lista las salas activas de un cine específico, ordenadas por nombre. El cine debe existir y estar activo.
     * @param cineId ID del cine para el cual se desean listar las salas. Debe ser un ID válido de un cine activo.
     * @return Lista de salas que pertenecen al cine especificado, cada una representada como un SalaPublicDTO.
     * @throws CineNoEncontradoException si el cine no existe.
     * @throws CineInactivoException si el cine está inactivo.
     */
    @Override
    public java.util.List<SalaPublicDTO> listarSalasPorCine(Long cineId) {
        var cine = cineRepository.findById(cineId)
                .orElseThrow(CineNoEncontradoException::new);
        if (!Boolean.TRUE.equals(cine.getActivo())) {
            throw new CineInactivoException();
        }

        return salaRepository.findByCineIdAndActivaTrueOrderByNombreAsc(cineId)
                .stream()
                .map(s -> new SalaPublicDTO(s.getId(), s.getNombre(), s.getTipo().name()))
                .toList();
    }
    /**
     * Lista las películas activas con opciones de filtrado por título, género y rango de fechas de estreno. El resultado se pagina y ordena por título.
     * @param q filtro opcional para buscar películas por título (mínimo 2 caracteres). Si es nulo o vacío, no se filtra por título.
     * @param generoId filtro opcional para buscar películas por género. Si es nulo, no se filtra por género.
     * @param desde filtro opcional para buscar películas estrenadas a partir de una fecha (formato "yyyy-MM-dd"). Si es nulo o vacío, no se filtra por fecha de estreno mínima.
     * @param hasta filtro opcional para buscar películas estrenadas hasta una fecha (formato "yyyy-MM-dd"). Si es nulo o vacío, no se filtra por fecha de estreno máxima.
     * @param page número de página para la paginación (0-based). Debe ser un número entero no negativo.
     * @param size tamaño de página para la paginación. Debe ser un número entero entre 1 y 50.
     * @return Página de películas que cumplen con los filtros especificados, cada una representada como un PeliculaCardDTO.
     * @throws FiltroInvalidoException si los parámetros de paginación son inválidos o si el filtro de título es demasiado corto.
     */
    @Override
    public Page<PeliculaCardDTO> listarPeliculas(String q, Long generoId, String desde, String hasta, int page, int size) {
        // Validaciones de calidad: filtros exactos y límites razonables
        if (page < 0) throw new FiltroInvalidoException("page no puede ser negativo.");
        if (size < 1 || size > 50) throw new FiltroInvalidoException("size debe estar entre 1 y 50.");
        if (q != null && !q.isBlank() && q.trim().length() < 2) {
            throw new FiltroInvalidoException("q debe tener al menos 2 caracteres.");
        }

        LocalDate d1 = (desde == null || desde.isBlank()) ? null : LocalDate.parse(desde);
        LocalDate d2 = (hasta == null || hasta.isBlank()) ? null : LocalDate.parse(hasta);
        if (d1 != null && d2 != null && d2.isBefore(d1)) {
            throw new FiltroInvalidoException("hasta no puede ser menor que desde.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("titulo").ascending());

        return peliculaRepository.search(
                        (q == null || q.isBlank()) ? null : q.trim(),
                        generoId,
                        d1,
                        d2,
                        // Solo películas activas
                        true,
                        pageable
                )
                .map(p -> new PeliculaCardDTO(
                        p.getId(),
                        p.getTitulo(),
                        p.getPosterUrl(),
                        p.getDuracionMin(),
                        p.getClasificacion(),
                        p.getFechaEstreno()
                ));
    }
    /**
     * Obtiene el detalle completo de una película específica por su ID. La película debe existir y estar activa.
     * @param peliculaId ID de la película para la cual se desea obtener el detalle. Debe ser un ID válido de una película activa.
     * @return Detalle completo de la película, representado como un PeliculaDetailDTO, incluyendo sus géneros asociados.
     * @throws PeliculaNoEncontradaException si la película no existe.
     */
    @Override
    public PeliculaDetailDTO detallePelicula(Long peliculaId) {
        var p = peliculaRepository.findById(peliculaId)
                .orElseThrow(PeliculaNoEncontradaException::new);

        Set<String> generos = (p.getGeneros() == null)
                ? Set.of()
                : p.getGeneros().stream().map(g -> g.getNombre()).collect(Collectors.toSet());

        return new PeliculaDetailDTO(
                p.getId(),
                p.getTitulo(),
                p.getSinopsis(),
                p.getDuracionMin(),
                p.getClasificacion(),
                p.getFechaEstreno(),
                p.getPosterUrl(),
                Boolean.TRUE.equals(p.getActiva()),
                generos
        );
    }
    /**
     * Lista las películas que se estrenarán en los próximos días, con un límite máximo de 365 días. El resultado se ordena por fecha de estreno.
     * @param dias número de días a partir de hoy para buscar estrenos próximos. Debe ser un número entero entre 1 y 365.
     * @return Lista de películas que se estrenarán en los próximos días, cada una representada como un PeliculaCardDTO.
     * @throws FiltroInvalidoException si el número de días es inválido.
     */
    @Override
    public java.util.List<PeliculaCardDTO> proximas(int dias) {
        if (dias < 1 || dias > 365) throw new FiltroInvalidoException("dias debe estar entre 1 y 365.");

        LocalDate hoy = LocalDate.now();
        LocalDate hasta = hoy.plusDays(dias);

        return peliculaRepository.findProximas(hoy, hasta).stream()
                .map(p -> new PeliculaCardDTO(p.getId(), p.getTitulo(), p.getPosterUrl(), p.getDuracionMin(),
                        p.getClasificacion(), p.getFechaEstreno()))
                .toList();
    }
    /**
     * Lista las películas que actualmente están en cartelera, es decir, aquellas que tienen funciones programadas a partir de hoy. El resultado se ordena por título.
     * @return Lista de películas que están en cartelera, cada una representada como un PeliculaCardDTO.
     */
    @Override
    public List<PeliculaCardDTO> enCartelera() {
        LocalDateTime now = LocalDateTime.now();

        return funcionRepository.findPeliculasEnCartelera(now)
                .stream()
                .map(p -> new PeliculaCardDTO(
                        p.getId(),
                        p.getTitulo(),
                        p.getPosterUrl(),
                        p.getDuracionMin(),
                        p.getClasificacion(),
                        p.getFechaEstreno()
                ))
                .toList();
    }
    /**
     * Lista los géneros disponibles en el sistema, ordenados por nombre. El resultado se representa como una lista de GeneroDTO.
     * @return Lista de géneros disponibles, cada uno representado como un GeneroDTO con su ID y nombre.
     */
    @Override
    public List<GeneroDTO> listarGeneros() {
        return generoRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(g -> new GeneroDTO(g.getId(), g.getNombre()))
                .toList();
    }
}