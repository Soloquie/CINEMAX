package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.GeneroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneroRepository extends JpaRepository<GeneroEntity, Long> {
    Optional<GeneroEntity> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}