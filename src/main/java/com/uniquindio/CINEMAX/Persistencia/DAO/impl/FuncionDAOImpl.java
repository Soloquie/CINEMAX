package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.FuncionDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.*;
import com.uniquindio.CINEMAX.Persistencia.Repository.FuncionRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.PeliculaRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.SalaRepository;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionUpsertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class FuncionDAOImpl implements FuncionDAO {

    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;

    @Override
    public FuncionResponseDTO crear(FuncionUpsertDTO dto) {
        PeliculaEntity pelicula = peliculaRepository.findById(dto.peliculaId())
                .orElseThrow(() -> new IllegalArgumentException("Película no existe"));

        SalaEntity sala = salaRepository.findById(dto.salaId())
                .orElseThrow(() -> new IllegalArgumentException("Sala no existe"));

        LocalDateTime inicio = dto.inicio();
        LocalDateTime fin = dto.fin();

        // Si no mandan fin, lo calculamos con duración de la película
        if (fin == null) {
            fin = inicio.plusMinutes(pelicula.getDuracionMin());
        }

        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La hora fin debe ser mayor que inicio");
        }

        FuncionEntity entity = FuncionEntity.builder()
                .pelicula(pelicula)
                .sala(sala)
                .inicio(inicio)
                .fin(fin)
                .idioma(dto.idioma())
                .subtitulos(dto.subtitulos() != null && dto.subtitulos())
                .precioBase(dto.precioBase() != null ? dto.precioBase() : java.math.BigDecimal.ZERO)
                .estado(EstadoFuncion.PROGRAMADA)
                .build();

        try {
            return toDTO(funcionRepository.save(entity));
        } catch (DataIntegrityViolationException ex) {
            // uk_sala_inicio
            throw new IllegalArgumentException("Ya existe una función en esa sala a esa hora de inicio");
        }
    }

    @Override
    public FuncionResponseDTO actualizar(Long id, FuncionUpsertDTO dto) {
        FuncionEntity entity = funcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Función no existe"));

        PeliculaEntity pelicula = peliculaRepository.findById(dto.peliculaId())
                .orElseThrow(() -> new IllegalArgumentException("Película no existe"));

        SalaEntity sala = salaRepository.findById(dto.salaId())
                .orElseThrow(() -> new IllegalArgumentException("Sala no existe"));

        LocalDateTime inicio = dto.inicio();
        LocalDateTime fin = dto.fin();
        if (fin == null) fin = inicio.plusMinutes(pelicula.getDuracionMin());

        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La hora fin debe ser mayor que inicio");
        }

        entity.setPelicula(pelicula);
        entity.setSala(sala);
        entity.setInicio(inicio);
        entity.setFin(fin);
        entity.setIdioma(dto.idioma());
        entity.setSubtitulos(dto.subtitulos() != null && dto.subtitulos());
        entity.setPrecioBase(dto.precioBase() != null ? dto.precioBase() : java.math.BigDecimal.ZERO);

        try {
            return toDTO(funcionRepository.save(entity));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Ya existe una función en esa sala a esa hora de inicio");
        }
    }

    @Override
    public FuncionResponseDTO cancelar(Long id) {
        FuncionEntity entity = funcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Función no existe"));
        entity.setEstado(EstadoFuncion.CANCELADA);
        return toDTO(funcionRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuncionResponseDTO> listarTodas() {
        return funcionRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuncionResponseDTO> listarProgramadasPorPelicula(Long peliculaId) {
        return funcionRepository
                .findByPeliculaIdAndEstadoAndInicioAfterOrderByInicioAsc(
                        peliculaId, EstadoFuncion.PROGRAMADA, LocalDateTime.now()
                )
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private FuncionResponseDTO toDTO(FuncionEntity f) {
        PeliculaEntity p = f.getPelicula();
        SalaEntity s = f.getSala();
        CineEntity c = s.getCine();

        return new FuncionResponseDTO(
                f.getId(),
                p.getId(),
                p.getTitulo(),
                p.getPosterUrl(),
                s.getId(),
                s.getNombre(),
                c.getId(),
                c.getNombre(),
                f.getInicio(),
                f.getFin(),
                f.getIdioma(),
                f.getSubtitulos(),
                f.getPrecioBase(),
                f.getEstado().name()
        );
    }
}