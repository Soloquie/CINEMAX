package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.PeliculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeliculaRepository extends JpaRepository<PeliculaEntity, Long> {
    List<PeliculaEntity> findByActivaTrueOrderByTituloAsc();
}