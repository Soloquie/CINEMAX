package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.PeliculaDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.GeneroEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.PeliculaEntity;
import com.uniquindio.CINEMAX.Persistencia.Mapper.PeliculaMapper;
import com.uniquindio.CINEMAX.Persistencia.Repository.GeneroRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.PeliculaRepository;
import com.uniquindio.CINEMAX.negocio.Model.Pelicula;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Transactional
public class PeliculaDAOImpl implements PeliculaDAO {

    private final PeliculaRepository peliculaRepository;
    private final GeneroRepository generoRepository;
    private final PeliculaMapper peliculaMapper;

    @Override
    public Pelicula guardar(Pelicula pelicula, List<Long> generoIds) {

        PeliculaEntity entity;

        if (pelicula.id() != null) {
            entity = peliculaRepository.findById(pelicula.id())
                    .orElseThrow(() -> new IllegalArgumentException("Película no existe"));
        } else {
            entity = new PeliculaEntity();
        }

        entity.setTitulo(pelicula.titulo());
        entity.setSinopsis(pelicula.sinopsis());
        entity.setDuracionMin(pelicula.duracionMin());
        entity.setClasificacion(pelicula.clasificacion());
        entity.setFechaEstreno(pelicula.fechaEstreno());
        entity.setPosterUrl(pelicula.posterUrl());
        entity.setActiva(pelicula.activa() != null ? pelicula.activa() : true);

        if (generoIds != null) {
            Set<GeneroEntity> generos = new HashSet<>(generoRepository.findAllById(generoIds));
            entity.setGeneros(generos);
        }

        return peliculaMapper.toDomain(peliculaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pelicula> buscarPorId(Long id) {
        return peliculaRepository.findById(id).map(peliculaMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pelicula> listarActivas() {
        return peliculaRepository.findByActivaTrueOrderByTituloAsc()
                .stream().map(peliculaMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pelicula> listarTodas() {
        return peliculaRepository.findAll().stream().map(peliculaMapper::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        PeliculaEntity entity = peliculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Película no existe"));
        entity.setActiva(false); // soft delete usando tu columna "activa"
        peliculaRepository.save(entity);
    }
}