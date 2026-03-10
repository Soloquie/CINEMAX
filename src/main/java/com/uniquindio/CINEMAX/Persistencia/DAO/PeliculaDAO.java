package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.negocio.Model.Pelicula;

import java.util.List;
import java.util.Optional;
/* Interfaz DAO para gestionar las operaciones relacionadas con las películas en el sistema CINEMAX. */
public interface PeliculaDAO {
    Pelicula guardar(Pelicula pelicula, List<Long> generoIds);
    Optional<Pelicula> buscarPorId(Long id);
    List<Pelicula> listarActivas();
    List<Pelicula> listarTodas();
    void eliminar(Long id); // soft delete
}