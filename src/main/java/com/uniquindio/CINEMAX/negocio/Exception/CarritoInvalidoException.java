package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un carrito se encuentra en un estado inválido para ejecutar una operación en el sistema CINEMAX. */
public class CarritoInvalidoException extends ConflictDomainException {

    public CarritoInvalidoException() {
        super("CARRITO_INVALIDO", "El carrito no se encuentra en un estado válido para esta operación.");
    }

    public CarritoInvalidoException(String message) {
        super("CARRITO_INVALIDO", message);
    }
}