package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un carrito requerido no existe en el sistema CINEMAX. */
public class CarritoNoEncontradoException extends NotFoundDomainException {

    public CarritoNoEncontradoException() {
        super("CARRITO_NO_ENCONTRADO", "El carrito no existe.");
    }

    public CarritoNoEncontradoException(String message) {
        super("CARRITO_NO_ENCONTRADO", message);
    }
}