package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.Model.Usuario;

import java.util.Optional;
/* Interfaz DAO para gestionar las operaciones relacionadas con los usuarios en el sistema CINEMAX. */
public interface UsuarioDAO {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(Long id);
    boolean existePorEmail(String email);
}