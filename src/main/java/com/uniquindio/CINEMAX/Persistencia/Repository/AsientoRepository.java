package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.AsientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsientoRepository extends JpaRepository<AsientoEntity, Long> {
    List<AsientoEntity> findBySalaId(Long salaId);
    List<AsientoEntity> findBySalaIdAndActivoTrue(Long salaId);
    long countBySalaIdAndActivoTrue(Long salaId);
}