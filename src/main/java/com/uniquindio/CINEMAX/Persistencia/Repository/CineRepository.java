package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.CineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas con los cines en el sistema CINEMAX.
 * Proporciona métodos personalizados para verificar la existencia de un cine por nombre y ciudad, buscar cines activos ordenados por ciudad y nombre,
 * y filtrar cines activos por ciudad con ordenamiento similar. Esto facilita la gestión de los cines disponibles en
 * el sistema, permitiendo realizar consultas específicas según las necesidades del negocio.
  */
public interface CineRepository extends JpaRepository<CineEntity, Long> {
    boolean existsByNombreAndCiudad(String nombre, String ciudad);
    List<CineEntity> findByActivoTrueOrderByCiudadAscNombreAsc();
    List<CineEntity> findByActivoTrueAndCiudadContainingIgnoreCaseOrderByCiudadAscNombreAsc(String ciudad);
}