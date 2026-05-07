package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/* Entidad JPA que representa una venta confirmada dentro del sistema CINEMAX.
 * Una venta se crea únicamente cuando el pago fue aprobado y la confirmación interna
 * logró marcar los asientos como vendidos, registrar boletas, productos y cerrar el carrito.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "ventas",
        indexes = {
                @Index(name = "idx_ventas_usuario", columnList = "usuario_id"),
                @Index(name = "idx_ventas_codigo", columnList = "codigo")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ventas_codigo", columnNames = "codigo"),
                @UniqueConstraint(name = "uk_ventas_pago", columnNames = "pago_id")
        }
)
public class VentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 120)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private UsuarioEntity usuario;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pago_id", nullable = false, unique = true)
    @ToString.Exclude
    private PagoEntity pago;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoVenta estado;

    @CreationTimestamp
    @Column(name = "creada_en", updatable = false)
    private LocalDateTime creadaEn;
}