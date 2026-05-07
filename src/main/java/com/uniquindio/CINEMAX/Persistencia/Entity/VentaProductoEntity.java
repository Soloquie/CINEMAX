package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
/* Entidad JPA que representa un producto de confitería vendido dentro de una venta confirmada.
 * Se guarda información histórica del producto, como nombre, cantidad, precio unitario y subtotal,
 * para que la venta conserve sus datos aunque luego el producto sea modificado por un administrador.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "venta_productos",
        indexes = {
                @Index(name = "idx_venta_productos_venta", columnList = "venta_id"),
                @Index(name = "idx_venta_productos_producto", columnList = "producto_id")
        }
)
public class VentaProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    @ToString.Exclude
    private VentaEntity venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    @ToString.Exclude
    private ProductoEntity producto;

    @Column(name = "nombre_producto", nullable = false, length = 150)
    private String nombreProducto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}