package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que ya existe un producto con el mismo SKU en el sistema CINEMAX. */
public class SkuDuplicadoException extends ConflictDomainException {

    public SkuDuplicadoException() {
        super("SKU_DUPLICADO", "Ya existe un producto con ese SKU.");
    }

    public SkuDuplicadoException(String message) {
        super("SKU_DUPLICADO", message);
    }
}