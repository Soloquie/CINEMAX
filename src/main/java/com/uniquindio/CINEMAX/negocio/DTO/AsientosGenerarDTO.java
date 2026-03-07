package com.uniquindio.CINEMAX.negocio.DTO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoAsiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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