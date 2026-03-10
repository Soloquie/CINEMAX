package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.AsientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas
* con los asientos en el sistema CINEMAX. Proporciona métodos personalizados para buscar asientos por sala,
* filtrar por asientos activos y contar el número de asientos activos en una sala específica.
  */
public interface AsientoRepository extends JpaRepository<AsientoEntity, Long> {
    List<AsientoEntity> findBySalaId(Long salaId);
    List<AsientoEntity> findBySalaIdAndActivoTrue(Long salaId);
    long countBySalaIdAndActivoTrue(Long salaId);
}