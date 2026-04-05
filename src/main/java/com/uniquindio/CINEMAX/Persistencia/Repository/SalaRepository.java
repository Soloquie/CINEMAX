package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.CineEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.SalaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas con
 * las salas de cine en el sistema CINEMAX.  Proporciona métodos personalizados para verificar la existencia de una
 * sala por nombre dentro de un cine específico, buscar salas por cine y filtrar salas activas ordenadas por nombre.
 * Esto facilita la gestión de las salas disponibles en cada cine, permitiendo realizar consultas específicas según las
 *  necesidades del negocio.
   */
public interface SalaRepository extends JpaRepository<SalaEntity, Long> {
    boolean existsByCineIdAndNombre(Long cineId, String nombre);
    List<SalaEntity> findByCineId(Long cineId);
    List<SalaEntity> findByCineIdAndActivaTrueOrderByNombreAsc(Long cineId);

}