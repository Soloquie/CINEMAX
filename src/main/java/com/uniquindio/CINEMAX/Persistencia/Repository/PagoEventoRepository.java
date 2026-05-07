package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.PagoEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/* Repositorio JPA para gestionar la bitácora de eventos recibidos desde el proveedor de pagos.
 * Facilita la auditoría del flujo de pago y la trazabilidad de notificaciones externas.
 */
public interface PagoEventoRepository extends JpaRepository<PagoEventoEntity, Long> {

    List<PagoEventoEntity> findByPagoIdOrderByRecibidoEnDesc(Long pagoId);
}