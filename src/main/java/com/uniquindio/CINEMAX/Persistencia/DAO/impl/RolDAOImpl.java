package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.RolDAO;
import com.uniquindio.CINEMAX.Persistencia.Mapper.RolMapper;
import com.uniquindio.CINEMAX.Persistencia.Repository.RolRepository;
import com.uniquindio.CINEMAX.negocio.Model.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
/* Implementación de la interfaz RolDAO para gestionar las operaciones relacionadas con los roles en el sistema CINEMAX. */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolDAOImpl implements RolDAO {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;
    /* Busca un rol por su nombre en el sistema CINEMAX.
     * @param nombre Nombre del rol a buscar.
     * @return Un Optional que contiene el rol encontrado, o vacío si no se encuentra ningún rol con ese nombre.
     */
    @Override
    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre).map(rolMapper::toDomain);
    }
}