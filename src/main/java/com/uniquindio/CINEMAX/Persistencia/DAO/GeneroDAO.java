package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.Model.Genero;

import java.util.List;
import java.util.Optional;
/* Interfaz DAO para gestionar las operaciones relacionadas con los géneros en el sistema CINEMAX. */
public interface GeneroDAO {
    Genero guardar(String nombre);
    List<Genero> listar();
    Optional<Genero> buscarPorId(Long id);
    void eliminar(Long id);
}