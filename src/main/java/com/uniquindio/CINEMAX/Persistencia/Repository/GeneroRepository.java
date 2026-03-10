package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.GeneroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas
* con los géneros de películas en el sistema CINEMAX. Proporciona métodos personalizados para buscar un género por nombre,
*  verificar la existencia de un género por nombre y obtener una lista de todos los géneros ordenados alfabéticamente por nombre.
* Esto facilita la gestión de los géneros disponibles en el sistema, permitiendo realizar consultas específicas
*  según las necesidades del negocio.
  */
public interface GeneroRepository extends JpaRepository<GeneroEntity, Long> {
    Optional<GeneroEntity> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<GeneroEntity> findAllByOrderByNombreAsc();
}