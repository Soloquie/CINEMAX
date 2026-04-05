package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
/* Entidad JPA que representa una película en el sistema CINEMAX. Cada película tiene un título, sinopsis, duración,
* clasificación, fecha de estreno y URL del póster.  La entidad también incluye un estado de actividad para indicar si la
* película está actualmente en cartelera, así como fechas de creación y actualización para fines de auditoría.
* Además, se establece una relación de muchos a muchos con la entidad Género para categorizar las películas
* según sus géneros cinematográficos.
  */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "peliculas")
public class PeliculaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="titulo", nullable=false, length=160)
    private String titulo;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name="sinopsis", columnDefinition = "TEXT")
    private String sinopsis;

    @Column(name="duracion_min", nullable=false)
    private Integer duracionMin;

    @Column(name="clasificacion", length=20)
    private String clasificacion;

    @Column(name="fecha_estreno")
    private LocalDate fechaEstreno;

    @Column(name="poster_url", length=400)
    private String posterUrl;

    @Builder.Default
    @Column(name="activa", nullable=false)
    private Boolean activa = true;

    @CreationTimestamp
    @Column(name="creado_en", updatable=false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name="actualizado_en")
    private LocalDateTime actualizadoEn;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pelicula_genero",
            joinColumns = @JoinColumn(name="pelicula_id"),
            inverseJoinColumns = @JoinColumn(name="genero_id")
    )
    @Builder.Default
    private Set<GeneroEntity> generos = new HashSet<>();
}