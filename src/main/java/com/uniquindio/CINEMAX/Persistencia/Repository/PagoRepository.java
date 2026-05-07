package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.PagoEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
/* Repositorio JPA para gestionar la persistencia de los pagos en el sistema CINEMAX.
 * Incluye consultas por referencia y bloqueo pesimista para proteger la confirmación
 * transaccional de pagos ante condiciones de concurrencia.
 */
public interface PagoRepository extends JpaRepository<PagoEntity, Long> {

    Optional<PagoEntity> findByReferencia(String referencia);

    Optional<PagoEntity> findByWompiTransactionId(String wompiTransactionId);

    boolean existsByReferencia(String referencia);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select p
        from PagoEntity p
        where p.referencia = :referencia
    """)
    Optional<PagoEntity> findByReferenciaForUpdate(@Param("referencia") String referencia);
}