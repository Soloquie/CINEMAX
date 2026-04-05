package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
/* Entidad JPA que representa una sala de cine en el sistema CINEMAX. Cada sala está asociada a un cine específico
 * y tiene un nombre único dentro de ese cine, un tipo (por ejemplo, 2D, 3D, IMAX) y un estado de actividad.
 * La entidad también registra la fecha de creación de la sala para fines de auditoría.
  */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "salas",
        uniqueConstraints = @UniqueConstraint(name="uk_sala_cine_nombre", columnNames = {"cine_id","nombre"})
)
public class SalaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="cine_id", nullable=false)
    @ToString.Exclude
    private CineEntity cine;

    @Column(name="nombre", nullable=false, length=60)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo", nullable=false, length=20)
    private TipoSala tipo;

    @Builder.Default
    @Column(name="activa", nullable=false)
    private Boolean activa = true;

    @CreationTimestamp
    @Column(name="creado_en", updatable=false)
    private LocalDateTime creadoEn;
}