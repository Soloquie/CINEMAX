package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
/* Entidad que representa un género de película en el sistema CINEMAX.
 * Cada género tiene un identificador único (id) y un nombre (nombre) que describe el tipo de película,
 *  como Acción, Comedia, Drama, etc.
 * La entidad está mapeada a la tabla "generos" en la base de datos,
 * con una restricción de unicidad en el campo "nombre" para evitar géneros duplicados.
  */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "generos", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class GeneroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre", nullable=false, length=60)
    private String nombre;
}