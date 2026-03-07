package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoFuncion;
import com.uniquindio.CINEMAX.Persistencia.Entity.FuncionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FuncionRepository extends JpaRepository<FuncionEntity, Long> {

    List<FuncionEntity> findByPeliculaIdAndEstadoAndInicioAfterOrderByInicioAsc(
            Long peliculaId, EstadoFuncion estado, LocalDateTime desde
    );
}