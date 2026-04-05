package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.FuncionAsientoEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas
* con los asientos de función en el sistema CINEMAX. Proporciona métodos personalizados para buscar asientos por función,
*  eliminar asientos por función, verificar la existencia de asientos para una función específica y realizar consultas
*  personalizadas para obtener los asientos de una función junto con sus detalles relacionados. Además, incluye métodos
*  para bloquear y liberar asientos utilizando bloqueos pesimistas y actualizaciones masivas, lo que es esencial para
*  gestionar la disponibilidad de los asientos durante el proceso de reserva.
  */
public interface FuncionAsientoRepository extends JpaRepository<FuncionAsientoEntity, Long> {
    List<FuncionAsientoEntity> findByFuncionId(Long funcionId);
    void deleteByFuncionId(Long funcionId);
    boolean existsByFuncionId(Long funcionId);

    @Query("""
           select fa
           from FuncionAsientoEntity fa
           join fetch fa.asiento a
           where fa.funcion.id = :funcionId
           order by a.fila asc, a.numero asc
           """)
    List<FuncionAsientoEntity> findAllByFuncionIdWithAsiento(@Param("funcionId") Long funcionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           select fa
           from FuncionAsientoEntity fa
           where fa.funcion.id = :funcionId and fa.id in :ids
           """)
    List<FuncionAsientoEntity> findForUpdate(@Param("funcionId") Long funcionId, @Param("ids") List<Long> ids);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
           update FuncionAsientoEntity fa
           set fa.estado = com.uniquindio.CINEMAX.Persistencia.Entity.EstadoFuncionAsiento.DISPONIBLE,
               fa.retenidoPor = null,
               fa.retencionExpira = null
           where fa.funcion.id = :funcionId
             and fa.estado = com.uniquindio.CINEMAX.Persistencia.Entity.EstadoFuncionAsiento.BLOQUEADO
             and fa.retencionExpira < :now
           """)
    int liberarExpirados(@Param("funcionId") Long funcionId, @Param("now") LocalDateTime now);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
           update FuncionAsientoEntity fa
           set fa.estado = com.uniquindio.CINEMAX.Persistencia.Entity.EstadoFuncionAsiento.DISPONIBLE,
               fa.retenidoPor = null,
               fa.retencionExpira = null
           where fa.funcion.id = :funcionId
             and fa.id in :ids
             and fa.estado = com.uniquindio.CINEMAX.Persistencia.Entity.EstadoFuncionAsiento.BLOQUEADO
             and fa.retenidoPor.id = :usuarioId
           """)
    int liberarPorUsuario(@Param("funcionId") Long funcionId, @Param("ids") List<Long> ids, @Param("usuarioId") Long usuarioId);
}