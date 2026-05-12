package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
/* Repositorio JPA para gestionar las ventas confirmadas en el sistema CINEMAX.
 * Permite consultar ventas por pago, código de venta y usuario propietario.
 */
public interface VentaRepository extends JpaRepository<VentaEntity, Long> {

    Optional<VentaEntity> findByPagoId(Long pagoId);

    Optional<VentaEntity> findByCodigo(String codigo);

    List<VentaEntity> findByUsuarioIdOrderByCreadaEnDesc(Long usuarioId);

    @Query("""
    select v
    from VentaEntity v
    join fetch v.pago p
    where v.usuario.email = :email
    order by v.id desc
""")
    List<VentaEntity> findMisComprasByUsuarioEmail(@Param("email") String email);
}