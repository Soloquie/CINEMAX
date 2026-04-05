package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
/* Entidad JPA que representa un usuario en el sistema CINEMAX. Cada usuario tiene un nombre, apellido, fecha de nacimiento,
 * email único, teléfono, contraseña (almacenada como hash), estado de verificación de email, estado de actividad,
 * fecha del último login, número de intentos fallidos y fecha hasta la cual está bloqueado (en caso de bloqueos por seguridad).
 * La entidad también incluye fechas de creación y actualización para fines de auditoría. Además, se establece una relación
 * de muchos a muchos con la entidad RolEntity para asignar roles a los usuarios del sistema.
   */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuarios_email", columnNames = {"email"})
        }
)
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 80)
    private String apellido;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "email", nullable = false, length = 180)
    private String email;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Builder.Default
    @Column(name = "email_verificado", nullable = false)
    private Boolean emailVerificado = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoUsuario estado;

    @Column(name = "ultimo_login_en")
    private LocalDateTime ultimoLoginEn;

    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    // usuarios <-> roles (tabla puente: usuario_roles)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<RolEntity> roles = new HashSet<>();
}