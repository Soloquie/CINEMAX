package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que una operación no está permitida de acuerdo con las reglas del sistema CINEMAX. */
public class OperacionNoPermitidaException extends ConflictDomainException {

    public OperacionNoPermitidaException() {
        super("OPERACION_NO_PERMITIDA", "La operación solicitada no está permitida.");
    }

    public OperacionNoPermitidaException(String message) {
        super("OPERACION_NO_PERMITIDA", message);
    }
}