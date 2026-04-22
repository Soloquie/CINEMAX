package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que una cantidad proporcionada no cumple con las reglas de validación del sistema CINEMAX. */
public class CantidadInvalidaException extends ValidationDomainException {

    public CantidadInvalidaException() {
        super("CANTIDAD_INVALIDA", "La cantidad proporcionada no es válida.");
    }

    public CantidadInvalidaException(String message) {
        super("CANTIDAD_INVALIDA", message);
    }
}