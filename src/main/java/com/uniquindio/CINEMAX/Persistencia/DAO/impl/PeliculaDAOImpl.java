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
/* Implementación de la interfaz PeliculaDAO para gestionar las operaciones relacionadas con las películas en el sistema CINEMAX. */
@Repository
@RequiredArgsConstructor
@Transactional
public class PeliculaDAOImpl implements PeliculaDAO {
    /* Repositorio para acceder a los datos de las películas en la base de datos. */
    private final PeliculaRepository peliculaRepository;
    private final GeneroRepository generoRepository;
    private final PeliculaMapper peliculaMapper;
    /* Guarda una película en el sistema CINEMAX. Si la película ya existe (tiene un ID), se actualiza; de lo contrario,
     se crea una nueva película. También se asocian los géneros correspondientes a la película.
     * @param pelicula Objeto Pelicula que contiene la información de la película a guardar o actualizar.
     * @param generoIds Lista de IDs de los géneros que se deben asociar con la película.
     * @return La película guardada o actualizada, incluyendo su ID y los géneros asociados.
     * @throws IllegalArgumentException Si se intenta actualizar una película que no existe.
     */
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
    /* Busca una película por su ID en el sistema CINEMAX.
     * @param id ID de la película a buscar.
     * @return Un Optional que contiene la película encontrada, o vacío si no se encuentra ninguna película con ese ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Pelicula> buscarPorId(Long id) {
        return peliculaRepository.findById(id).map(peliculaMapper::toDomain);
    }
    /* Lista todas las películas activas en el sistema CINEMAX, ordenadas por título de forma ascendente.
     * @return Una lista de películas activas, cada una con su ID, título, sinopsis, duración, clasificación, fecha de estreno,
     * URL del póster y estado activo.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Pelicula> listarActivas() {
        return peliculaRepository.findByActivaTrueOrderByTituloAsc()
                .stream().map(peliculaMapper::toDomain).toList();
    }
    /* Lista todas las películas disponibles en el sistema CINEMAX, sin importar su estado activo.
     * @return Una lista de películas, cada una con su ID, título, sinopsis, duración, clasificación, fecha de estreno,
     * URL del póster y estado activo.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Pelicula> listarTodas() {
        return peliculaRepository.findAll().stream().map(peliculaMapper::toDomain).toList();
    }
    /* Elimina una película del sistema CINEMAX de forma lógica (soft delete) estableciendo su estado activo a false.
     * Verifica que la película exista antes de eliminarla.
     * @param id ID de la película que se desea eliminar.
     */
    @Override
    public void eliminar(Long id) {
        PeliculaEntity entity = peliculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Película no existe"));
        entity.setActiva(false); // soft delete usando tu columna "activa"
        peliculaRepository.save(entity);
    }
}