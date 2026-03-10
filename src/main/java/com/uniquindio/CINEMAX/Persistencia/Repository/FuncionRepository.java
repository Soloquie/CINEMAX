package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoFuncion;
import com.uniquindio.CINEMAX.Persistencia.Entity.FuncionEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.PeliculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas
* con las funciones en el sistema CINEMAX.Proporciona métodos personalizados para buscar funciones por película,
*  estado y fecha de inicio, obtener películas en cartelera basadas en funciones programadas,  y realizar búsquedas
* avanzadas de funciones utilizando múltiples criterios como estado, rango de fechas, cine y película.
* Esto facilita la gestión de las funciones disponibles en el sistema, permitiendo realizar consultas específicas según las necesidades del negocio.
  */
public interface FuncionRepository extends JpaRepository<FuncionEntity, Long> {

    List<FuncionEntity> findByPeliculaIdAndEstadoAndInicioAfterOrderByInicioAsc(
            Long peliculaId, EstadoFuncion estado, LocalDateTime desde
    );

    @Query("""
    select distinct p
    from FuncionEntity f
    join f.pelicula p
    where p.activa = true
      and f.estado = com.uniquindio.CINEMAX.Persistencia.Entity.EstadoFuncion.PROGRAMADA
      and f.inicio > :now
    order by p.titulo asc
""")
    List<PeliculaEntity> findPeliculasEnCartelera(@Param("now") LocalDateTime now);

    @Query("""
    select f
    from FuncionEntity f
    join fetch f.pelicula p
    join fetch f.sala s
    join fetch s.cine c
    where f.estado = :estado
      and (:desde is null or f.inicio >= :desde)
      and (:hasta is null or f.inicio < :hasta)
      and (:cineId is null or c.id = :cineId)
      and (:peliculaId is null or p.id = :peliculaId)
    order by f.inicio asc
""")
    List<FuncionEntity> buscarFunciones(
            @Param("estado") EstadoFuncion estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("cineId") Long cineId,
            @Param("peliculaId") Long peliculaId
    );
}