package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.CarritoItemEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.TipoCarritoItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarritoItemRepository extends JpaRepository<CarritoItemEntity, Long> {

    List<CarritoItemEntity> findByCarritoIdAndTipoAndFuncionAsientoIdIn(
            Long carritoId, TipoCarritoItem tipo, List<Long> funcionAsientoIds
    );

    boolean existsByCarritoIdAndTipoAndFuncionAsientoId(Long carritoId, TipoCarritoItem tipo, Long funcionAsientoId);

    void deleteByCarritoIdAndTipoAndFuncionAsientoIdIn(
            Long carritoId, TipoCarritoItem tipo, List<Long> funcionAsientoIds
    );

    @Query("""
           select ci
           from CarritoItemEntity ci
           left join fetch ci.funcionAsiento fa
           left join fetch fa.asiento a
           left join fetch fa.funcion f
           left join fetch f.pelicula p
           left join fetch f.sala s
           left join fetch s.cine c
           where ci.carrito.id = :carritoId
           order by ci.id
           """)
    List<CarritoItemEntity> findItemsWithDetails(@Param("carritoId") Long carritoId);
}