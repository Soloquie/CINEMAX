package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.CarritoItemEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.TipoCarritoItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas con
* los items del carrito en el sistema CINEMAX. Proporciona métodos personalizados para buscar items por carrito, tipo
* y asiento de función, verificar la existencia de un item específico y eliminar items según ciertos criterios. Además,
*  incluye una consulta personalizada para obtener los items del carrito junto con sus detalles relacionados,
*  como la función, el asiento, la película, la sala y el cine asociados.
 */
public interface CarritoItemRepository extends JpaRepository<CarritoItemEntity, Long> {

    List<CarritoItemEntity> findByCarritoIdAndTipoAndFuncionAsientoIdIn(
            Long carritoId, TipoCarritoItem tipo, List<Long> funcionAsientoIds
    );

    boolean existsByCarritoIdAndTipoAndFuncionAsientoId(Long carritoId, TipoCarritoItem tipo, Long funcionAsientoId);

    void deleteByCarritoIdAndTipoAndFuncionAsientoIdIn(
            Long carritoId, TipoCarritoItem tipo, List<Long> funcionAsientoIds
    );

    java.util.Optional<CarritoItemEntity> findByCarritoIdAndTipoAndProductoId(
            Long carritoId, TipoCarritoItem tipo, Long productoId
    );

    List<CarritoItemEntity> findByCarritoIdAndTipoAndProductoIdIn(
            Long carritoId, TipoCarritoItem tipo, List<Long> productoIds
    );

    boolean existsByCarritoIdAndTipoAndProductoId(Long carritoId, TipoCarritoItem tipo, Long productoId);


    void deleteByCarritoIdAndTipoAndProductoId(Long carritoId, TipoCarritoItem tipo, Long productoId);

    @Query("""
       select ci
       from CarritoItemEntity ci
       left join fetch ci.funcionAsiento fa
       left join fetch fa.asiento a
       left join fetch fa.funcion f
       left join fetch f.pelicula p
       left join fetch f.sala s
       left join fetch s.cine c
       left join fetch ci.producto pr
       where ci.carrito.id = :carritoId
       order by ci.id
       """)
    List<CarritoItemEntity> findItemsWithDetails(@Param("carritoId") Long carritoId);

    List<CarritoItemEntity> findByCarritoIdAndTipo(Long carritoId, TipoCarritoItem tipo);

    void deleteByCarritoIdAndTipo(Long carritoId, TipoCarritoItem tipo);
}