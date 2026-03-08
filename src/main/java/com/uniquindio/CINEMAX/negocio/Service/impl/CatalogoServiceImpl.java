package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.Repository.*;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoServiceImpl implements CatalogoService {

    private final CineRepository cineRepository;
    private final SalaRepository salaRepository;
    private final PeliculaRepository peliculaRepository;
    private final FuncionRepository funcionRepository;
    private final GeneroRepository generoRepository;

    @Override
    public java.util.List<CinePublicDTO> listarCines(String ciudad) {
        if (ciudad != null && !ciudad.isBlank() && ciudad.trim().length() < 2) {
            throw new IllegalArgumentException("El filtro ciudad debe tener al menos 2 caracteres.");
        }

        var cines = (ciudad == null || ciudad.isBlank())
                ? cineRepository.findByActivoTrueOrderByCiudadAscNombreAsc()
                : cineRepository.findByActivoTrueAndCiudadContainingIgnoreCaseOrderByCiudadAscNombreAsc(ciudad.trim());

        return cines.stream()
                .map(c -> new CinePublicDTO(c.getId(), c.getNombre(), c.getCiudad(), c.getDireccion()))
                .toList();
    }

    @Override
    public java.util.List<SalaPublicDTO> listarSalasPorCine(Long cineId) {
        var cine = cineRepository.findById(cineId)
                .orElseThrow(() -> new IllegalArgumentException("Cine no existe."));
        if (!Boolean.TRUE.equals(cine.getActivo())) {
            throw new IllegalArgumentException("El cine está inactivo.");
        }

        return salaRepository.findByCineIdAndActivaTrueOrderByNombreAsc(cineId)
                .stream()
                .map(s -> new SalaPublicDTO(s.getId(), s.getNombre(), s.getTipo().name()))
                .toList();
    }

    @Override
    public Page<PeliculaCardDTO> listarPeliculas(String q, Long generoId, String desde, String hasta, int page, int size) {
        // Validaciones de calidad: filtros exactos y límites razonables
        if (page < 0) throw new IllegalArgumentException("page no puede ser negativo.");
        if (size < 1 || size > 50) throw new IllegalArgumentException("size debe estar entre 1 y 50.");
        if (q != null && !q.isBlank() && q.trim().length() < 2) {
            throw new IllegalArgumentException("q debe tener al menos 2 caracteres.");
        }

        LocalDate d1 = (desde == null || desde.isBlank()) ? null : LocalDate.parse(desde);
        LocalDate d2 = (hasta == null || hasta.isBlank()) ? null : LocalDate.parse(hasta);
        if (d1 != null && d2 != null && d2.isBefore(d1)) {
            throw new IllegalArgumentException("hasta no puede ser menor que desde.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("titulo").ascending());

        return peliculaRepository.search(
                        (q == null || q.isBlank()) ? null : q.trim(),
                        generoId,
                        d1,
                        d2,
                        true, // soloActivas=true para cliente
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

    @Override
    public PeliculaDetailDTO detallePelicula(Long peliculaId) {
        var p = peliculaRepository.findById(peliculaId)
                .orElseThrow(() -> new IllegalArgumentException("Película no existe."));

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

    @Override
    public java.util.List<PeliculaCardDTO> proximas(int dias) {
        if (dias < 1 || dias > 365) throw new IllegalArgumentException("dias debe estar entre 1 y 365.");

        LocalDate hoy = LocalDate.now();
        LocalDate hasta = hoy.plusDays(dias);

        return peliculaRepository.findProximas(hoy, hasta).stream()
                .map(p -> new PeliculaCardDTO(p.getId(), p.getTitulo(), p.getPosterUrl(), p.getDuracionMin(),
                        p.getClasificacion(), p.getFechaEstreno()))
                .toList();
    }

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

    @Override
    public List<GeneroDTO> listarGeneros() {
        return generoRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(g -> new GeneroDTO(g.getId(), g.getNombre()))
                .toList();
    }
}