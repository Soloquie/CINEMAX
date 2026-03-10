package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
/* Entidad JPA que representa un cine en el sistema CINEMAX. Cada cine tiene un nombre, ciudad, dirección y un estado de actividad.
 * La entidad también registra la fecha de creación del cine para fines de auditoría.
  */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "cines")
public class CineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre", nullable=false, length=120)
    private String nombre;

    @Column(name="ciudad", nullable=false, length=80)
    private String ciudad;

    @Column(name="direccion", nullable=false, length=180)
    private String direccion;

    @Builder.Default
    @Column(name="activo", nullable=false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name="creado_en", updatable=false)
    private LocalDateTime creadoEn;
}