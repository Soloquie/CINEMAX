package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.GeneroDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.GeneroEntity;
import com.uniquindio.CINEMAX.Persistencia.Mapper.GeneroMapper;
import com.uniquindio.CINEMAX.Persistencia.Repository.GeneroRepository;
import com.uniquindio.CINEMAX.negocio.Model.Genero;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
/* Implementación de la interfaz GeneroDAO para gestionar las operaciones relacionadas con los géneros en el sistema CINEMAX. */
@Repository
@RequiredArgsConstructor
@Transactional
public class GeneroDAOImpl implements GeneroDAO {

    private final GeneroRepository generoRepository;
    private final GeneroMapper generoMapper;
    /* Guarda un nuevo género en el sistema CINEMAX. Verifica que no exista un género con el mismo nombre antes de guardarlo.
     * @param nombre Nombre del género a guardar.
     * @return El género guardado, incluyendo su ID y nombre.
     * @throws IllegalArgumentException Si ya existe un género con el mismo nombre.
     */
    @Override
    public Genero guardar(String nombre) {
        if (generoRepository.existsByNombre(nombre)) {
            throw new IllegalArgumentException("Ya existe ese género");
        }
        GeneroEntity saved = generoRepository.save(GeneroEntity.builder().nombre(nombre).build());
        return generoMapper.toDomain(saved);
    }
    /* Lista todos los géneros disponibles en el sistema CINEMAX.
     * @return Una lista de géneros, cada uno con su ID y nombre.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Genero> listar() {
        return generoRepository.findAll().stream().map(generoMapper::toDomain).toList();
    }
    /* Busca un género por su ID en el sistema CINEMAX.
     * @param id ID del género a buscar.
     * @return Un Optional que contiene el género encontrado, o vacío si no se encuentra ningún género con ese ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Genero> buscarPorId(Long id) {
        return generoRepository.findById(id).map(generoMapper::toDomain);
    }
    /* Elimina un género por su ID en el sistema CINEMAX.
     * @param id ID del género a eliminar.
     */
    @Override
    public void eliminar(Long id) {
        generoRepository.deleteById(id);
    }
}