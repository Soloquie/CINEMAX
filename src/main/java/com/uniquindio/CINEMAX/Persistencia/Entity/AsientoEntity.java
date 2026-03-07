package com.uniquindio.CINEMAX.Persistencia.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "asientos",
        uniqueConstraints = @UniqueConstraint(name="uk_asiento_sala_fila_num", columnNames = {"sala_id","fila","numero"})
)
public class AsientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    @ToString.Exclude
    private SalaEntity sala;

    @Column(name = "fila", nullable = false, length = 5)
    private String fila;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoAsiento tipo;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}