package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.SalaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaRepository extends JpaRepository<SalaEntity, Long> {
    boolean existsByCineIdAndNombre(Long cineId, String nombre);
    List<SalaEntity> findByCineId(Long cineId);
}