package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.CineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CineRepository extends JpaRepository<CineEntity, Long> {
    boolean existsByNombreAndCiudad(String nombre, String ciudad);
    List<CineEntity> findByActivoTrueOrderByCiudadAscNombreAsc();
    List<CineEntity> findByActivoTrueAndCiudadContainingIgnoreCaseOrderByCiudadAscNombreAsc(String ciudad);
}