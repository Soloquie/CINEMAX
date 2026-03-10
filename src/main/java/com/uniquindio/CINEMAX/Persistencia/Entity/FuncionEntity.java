package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/* Entidad JPA que representa una función de cine en el sistema CINEMAX. Cada función está asociada a una película y una sala específica,
 * y tiene información sobre el horario de inicio y fin, idioma, subtítulos, precio base y estado de la función. La entidad también registra
 * la fecha de creación para fines de auditoría. Se establece una restricción única para evitar funciones superpuestas en la misma sala.
  */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "funciones",
        uniqueConstraints = @UniqueConstraint(name="uk_sala_inicio", columnNames = {"sala_id","inicio"})
)
public class FuncionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="pelicula_id", nullable=false)
    @ToString.Exclude
    private PeliculaEntity pelicula;

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="sala_id", nullable=false)
    @ToString.Exclude
    private SalaEntity sala;

    @Column(name="inicio", nullable=false)
    private LocalDateTime inicio;

    @Column(name="fin", nullable=false)
    private LocalDateTime fin;

    @Column(name="idioma", length=40)
    private String idioma;

    @Builder.Default
    @Column(name="subtitulos", nullable=false)
    private Boolean subtitulos = false;

    @Builder.Default
    @Column(name="precio_base", nullable=false, precision=10, scale=2)
    private BigDecimal precioBase = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable=false, length=20)
    private EstadoFuncion estado;

    @CreationTimestamp
    @Column(name="creado_en", updatable=false)
    private LocalDateTime creadoEn;
}