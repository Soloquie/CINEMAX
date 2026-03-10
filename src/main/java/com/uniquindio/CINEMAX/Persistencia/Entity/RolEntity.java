package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
/* Entidad JPA que representa un rol de usuario en el sistema CINEMAX. Cada rol tiene un nombre único,
 * una descripción opcional y una fecha de creación.  La entidad también establece una relación de muchos a muchos
 *  con la entidad UsuarioEntity para asignar roles a los usuarios del sistema.
  */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private String nombre; // ADMIN, CLIENTE

    @Column(name = "descripcion", length = 120)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    // Bidireccional (opcional, pero útil)
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<UsuarioEntity> usuarios = new HashSet<>();
}