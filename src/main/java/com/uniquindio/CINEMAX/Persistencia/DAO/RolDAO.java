package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.Model.Rol;

import java.util.Optional;

public interface RolDAO {
    Optional<Rol> buscarPorNombre(String nombre); // "CLIENTE", "ADMIN"
}