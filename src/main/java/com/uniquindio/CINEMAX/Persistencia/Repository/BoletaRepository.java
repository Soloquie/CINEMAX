package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.BoletaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
/* Repositorio JPA para gestionar las boletas generadas a partir de ventas confirmadas.
 * Permite consultar boletas por venta, código y asiento de función asociado.
 */
public interface BoletaRepository extends JpaRepository<BoletaEntity, Long> {

    List<BoletaEntity> findByVentaId(Long ventaId);

    Optional<BoletaEntity> findByCodigo(String codigo);

    boolean existsByFuncionAsientoId(Long funcionAsientoId);
}