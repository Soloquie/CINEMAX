package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/* Entidad JPA que representa una boleta generada después de una venta confirmada.
 * Cada boleta queda asociada a un asiento específico de una función y conserva datos
 * relevantes de la compra para facilitar la consulta histórica por parte del usuario.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "boletas",
        indexes = {
                @Index(name = "idx_boletas_venta", columnList = "venta_id"),
                @Index(name = "idx_boletas_codigo", columnList = "codigo")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_boletas_codigo", columnNames = "codigo"),
                @UniqueConstraint(name = "uk_boletas_funcion_asiento", columnNames = "funcion_asiento_id")
        }
)
public class BoletaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 120)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    @ToString.Exclude
    private VentaEntity venta;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcion_asiento_id", nullable = false, unique = true)
    @ToString.Exclude
    private FuncionAsientoEntity funcionAsiento;

    @Column(name = "pelicula_titulo", nullable = false, length = 200)
    private String peliculaTitulo;

    @Column(name = "cine_nombre", nullable = false, length = 150)
    private String cineNombre;

    @Column(name = "sala_nombre", nullable = false, length = 100)
    private String salaNombre;

    @Column(name = "fila", nullable = false, length = 10)
    private String fila;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "inicio_funcion", nullable = false)
    private LocalDateTime inicioFuncion;

    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @CreationTimestamp
    @Column(name = "creada_en", updatable = false)
    private LocalDateTime creadaEn;
}