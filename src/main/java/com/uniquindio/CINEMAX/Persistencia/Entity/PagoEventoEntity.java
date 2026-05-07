package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
/* Entidad JPA que representa un evento recibido desde el proveedor de pagos.
 * Esta bitácora permite auditar las notificaciones recibidas, conservar el payload original
 * y evitar pérdida de trazabilidad en el flujo de confirmación de pagos.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "pago_eventos",
        indexes = {
                @Index(name = "idx_pago_eventos_pago", columnList = "pago_id"),
                @Index(name = "idx_pago_eventos_transaction", columnList = "transaction_id")
        }
)
public class PagoEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Puede ser null si llega un evento que aún no se puede asociar a un pago interno. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id")
    @ToString.Exclude
    private PagoEntity pago;

    @Column(name = "proveedor", nullable = false, length = 30)
    private String proveedor;

    @Column(name = "event_type", length = 120)
    private String eventType;

    @Column(name = "transaction_id", length = 120)
    private String transactionId;

    @Column(name = "estado_transaccion", length = 50)
    private String estadoTransaccion;

    @Lob
    @Column(name = "payload_json", columnDefinition = "LONGTEXT")
    private String payloadJson;

    @CreationTimestamp
    @Column(name = "recibido_en", updatable = false)
    private LocalDateTime recibidoEn;
}