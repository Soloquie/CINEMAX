package com.uniquindio.CINEMAX.negocio.DTO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoAsiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
/**
 * DTO (Data Transfer Object) para representar la información necesaria para generar asientos en una función de cine.
 * Este DTO se utiliza para recibir la información de las filas, el número de asientos por fila, el tipo de asiento
 * por defecto, las filas VIP y las posiciones de asientos para personas con discapacidad. Contiene validaciones
 * para asegurar que las filas no estén vacías, que el número de asientos por fila sea mayor o igual a 1,
 * y que el tipo de asiento por defecto no sea nulo.
 */
@Data
public class AsientosGenerarDTO {

    @NotEmpty
    private List<String> filas = new ArrayList<>();   // ej: ["A","B","C","D","E"]

    @Min(1)
    private int asientosPorFila;                      // ej: 10

    @NotNull
    private TipoAsiento tipoDefault = TipoAsiento.STANDARD; // STANDARD/VIP/DISCAPACIDAD

    // Opcional: filas VIP (se ponen tipo VIP aunque default sea STANDARD)
    private List<String> vipFilas = new ArrayList<>();

    private List<AsientoPosDTO> discapacidad = new ArrayList<>();

    private boolean desactivarFuera = true;
}