package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un producto requerido no existe en el sistema CINEMAX. */
public class ProductoNoEncontradoException extends NotFoundDomainException {

    public ProductoNoEncontradoException() {
        super("PRODUCTO_NO_ENCONTRADO", "El producto no existe.");
    }

    public ProductoNoEncontradoException(String message) {
        super("PRODUCTO_NO_ENCONTRADO", message);
    }
}