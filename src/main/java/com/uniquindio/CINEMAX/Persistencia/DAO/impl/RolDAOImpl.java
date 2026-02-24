package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.RolDAO;
import com.uniquindio.CINEMAX.Persistencia.Mapper.RolMapper;
import com.uniquindio.CINEMAX.Persistencia.Repository.RolRepository;
import com.uniquindio.CINEMAX.negocio.Model.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolDAOImpl implements RolDAO {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    @Override
    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre).map(rolMapper::toDomain);
    }
}