package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
/* Entidad JPA que representa un token de usuario en el sistema CINEMAX. Cada token está asociado a un usuario específico
 * y tiene un valor único, un tipo (por ejemplo, CONFIRMACION_REGISTRO, RECUPERACION_CONTRASENA) y fechas de creación,
 *  expiración y uso. La entidad también establece una relación de muchos a uno con la entidad UsuarioEntity para
 *  vincular cada token a su respectivo usuario.
  */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "tokens_usuario",
        indexes = {
                @Index(name = "idx_token_usuario_tipo", columnList = "usuario_id,tipo"),
                @Index(name = "idx_token_expira", columnList = "expira_en")
        }
)
public class TokenUsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchos tokens para 1 usuario
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private UsuarioEntity usuario;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoToken tipo;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "usado_en")
    private LocalDateTime usadoEn;
}