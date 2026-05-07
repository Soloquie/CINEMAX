package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.VentaProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/* Repositorio JPA para gestionar los productos de confitería asociados a una venta confirmada.
 * Permite consultar el detalle de productos vendidos por cada venta.
 */
public interface VentaProductoRepository extends JpaRepository<VentaProductoEntity, Long> {

    List<VentaProductoEntity> findByVentaId(Long ventaId);
}