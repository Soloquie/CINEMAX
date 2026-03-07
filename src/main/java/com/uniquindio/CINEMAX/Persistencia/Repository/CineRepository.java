package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.CineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CineRepository extends JpaRepository<CineEntity, Long> {}