package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @Lob
    @Column(name="sinopsis")
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