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

@Repository
@RequiredArgsConstructor
@Transactional
public class GeneroDAOImpl implements GeneroDAO {

    private final GeneroRepository generoRepository;
    private final GeneroMapper generoMapper;

    @Override
    public Genero guardar(String nombre) {
        if (generoRepository.existsByNombre(nombre)) {
            throw new IllegalArgumentException("Ya existe ese género");
        }
        GeneroEntity saved = generoRepository.save(GeneroEntity.builder().nombre(nombre).build());
        return generoMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genero> listar() {
        return generoRepository.findAll().stream().map(generoMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Genero> buscarPorId(Long id) {
        return generoRepository.findById(id).map(generoMapper::toDomain);
    }

    @Override
    public void eliminar(Long id) {
        generoRepository.deleteById(id);
    }
}