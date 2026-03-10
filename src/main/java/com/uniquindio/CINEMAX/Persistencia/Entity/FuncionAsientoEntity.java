package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
/* Entidad JPA que representa la relación entre una función y un asiento en el sistema CINEMAX.
 * Esta entidad gestiona el estado de cada asiento para una función específica, incluyendo si el asiento está disponible,
 *  reservado o vendido.
 * También permite gestionar retenciones temporales de asientos por parte de usuarios durante el proceso de compra.
  */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "funcion_asientos",
        uniqueConstraints = @UniqueConstraint(name="uk_funcion_asiento", columnNames = {"funcion_id","asiento_id"})
)
public class FuncionAsientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcion_id", nullable = false)
    @ToString.Exclude
    private FuncionEntity funcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asiento_id", nullable = false)
    @ToString.Exclude
    private AsientoEntity asiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoFuncionAsiento estado;

    // opcional por ahora (luego lo usamos para "hold")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retenido_por")
    @ToString.Exclude
    private UsuarioEntity retenidoPor;

    @Column(name = "retencion_expira")
    private LocalDateTime retencionExpira;

    @UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}