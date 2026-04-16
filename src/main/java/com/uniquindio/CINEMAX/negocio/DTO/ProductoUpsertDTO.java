package com.uniquindio.CINEMAX.negocio.DTO;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductoUpsertDTO(

        @NotBlank(message = "El SKU es obligatorio")
        @Size(max = 60, message = "El SKU no puede exceder 60 caracteres")
        String sku,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 140, message = "El nombre no puede exceder 140 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero")
        BigDecimal precio,

        @NotBlank(message = "La categoría es obligatoria")
        String categoria,

        String imagenUrl,

        Boolean activo,

        @NotNull(message = "El stock inicial es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @NotNull(message = "El stock mínimo es obligatorio")
        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        Integer stockMinimo
) {}