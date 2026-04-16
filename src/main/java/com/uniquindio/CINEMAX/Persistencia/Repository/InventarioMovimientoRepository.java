package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.InventarioMovimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioMovimientoRepository extends JpaRepository<InventarioMovimientoEntity, Long> {
}