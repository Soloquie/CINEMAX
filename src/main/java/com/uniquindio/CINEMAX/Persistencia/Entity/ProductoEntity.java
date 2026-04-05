package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/* Entidad JPA que representa un producto en el sistema CINEMAX. Cada producto tiene un SKU único, un nombre,
 un precio y un estado de actividad.
 * La entidad también registra la fecha de creación del producto para fines de auditoría.
  */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "productos")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="sku", nullable=false, length=60)
    private String sku;

    @Column(name="nombre", nullable=false, length=140)
    private String nombre;

    @Column(name="precio", nullable=false, precision=10, scale=2)
    private BigDecimal precio;

    @Builder.Default
    @Column(name="activo", nullable=false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name="creado_en", updatable=false)
    private LocalDateTime creadoEn;
}