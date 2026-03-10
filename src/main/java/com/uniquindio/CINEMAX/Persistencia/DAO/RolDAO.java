package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.Model.Rol;

import java.util.Optional;
/* Interfaz DAO para gestionar las operaciones relacionadas con los roles en el sistema CINEMAX. */
public interface RolDAO {
    Optional<Rol> buscarPorNombre(String nombre); // "CLIENTE", "ADMIN"
}