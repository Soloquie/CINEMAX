package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.CarritoEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {
    Optional<CarritoEntity> findFirstByUsuarioIdAndEstadoOrderByActualizadoEnDesc(Long usuarioId, EstadoCarrito estado);
}