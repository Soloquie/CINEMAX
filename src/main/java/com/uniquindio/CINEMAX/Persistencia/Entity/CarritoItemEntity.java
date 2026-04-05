package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/* Entidad JPA que representa un ítem dentro de un carrito de compras en el sistema CINEMAX. Cada ítem puede ser
 * de tipo función (asiento reservado para una función específica) o producto (artículo del concesionario). El ítem
 * está asociado a un carrito específico y contiene información sobre la cantidad, el precio unitario y la fecha de creación.
  */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "carrito_items")
public class CarritoItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id", nullable = false)
    @ToString.Exclude
    private CarritoEntity carrito;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCarritoItem tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcion_asiento_id")
    @ToString.Exclude
    private FuncionAsientoEntity funcionAsiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @ToString.Exclude
    private ProductoEntity producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;
}