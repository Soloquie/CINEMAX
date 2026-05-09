package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/* Entidad JPA que representa un intento de pago dentro del sistema CINEMAX.
 * Esta entidad almacena la referencia única del pago, el proveedor utilizado, el estado del pago,
 * el monto calculado por el backend y la relación con el usuario y el carrito asociado.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "pagos",
        indexes = {
                @Index(name = "idx_pagos_referencia", columnList = "referencia"),
                @Index(name = "idx_pagos_usuario_estado", columnList = "usuario_id, estado"),
                @Index(name = "idx_pagos_wompi_transaction", columnList = "wompi_transaction_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pagos_referencia", columnNames = "referencia")
        }
)
public class PagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Usuario dueño del pago. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private UsuarioEntity usuario;

    /* Carrito que originó el pago. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id", nullable = false)
    @ToString.Exclude
    private CarritoEntity carrito;

    @Column(name = "referencia", nullable = false, unique = true, length = 120)
    private String referencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "proveedor", nullable = false, length = 30)
    private ProveedorPago proveedor;

    @Column(name = "wompi_transaction_id", length = 120)
    private String wompiTransactionId;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "monto_centavos", nullable = false)
    private Long montoCentavos;

    @Builder.Default
    @Column(name = "moneda", nullable = false, length = 10)
    private String moneda = "COP";

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoPago estado;

    @Column(name = "mensaje_error", length = 500)
    private String mensajeError;

    @Column(name = "confirmado_en")
    private LocalDateTime confirmadoEn;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "provider_preference_id", length = 120)
    private String providerPreferenceId;

    @Column(name = "provider_payment_id", length = 120)
    private String providerPaymentId;
}