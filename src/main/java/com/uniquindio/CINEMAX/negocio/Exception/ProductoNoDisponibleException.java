package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un producto no está disponible para su operación en el sistema CINEMAX. */
public class ProductoNoDisponibleException extends ConflictDomainException {

    public ProductoNoDisponibleException() {
        super("PRODUCTO_NO_DISPONIBLE", "El producto no está disponible.");
    }

    public ProductoNoDisponibleException(String message) {
        super("PRODUCTO_NO_DISPONIBLE", message);
    }
}