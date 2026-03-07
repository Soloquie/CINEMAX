package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.FuncionAsientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuncionAsientoRepository extends JpaRepository<FuncionAsientoEntity, Long> {
    List<FuncionAsientoEntity> findByFuncionId(Long funcionId);
    void deleteByFuncionId(Long funcionId);
    boolean existsByFuncionId(Long funcionId);
}