package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un pago no se encuentra en un estado válido para ejecutar una operación. */
public class PagoInvalidoException extends ConflictDomainException {

    public PagoInvalidoException() {
        super("PAGO_INVALIDO", "El pago no se encuentra en un estado válido para esta operación.");
    }

    public PagoInvalidoException(String message) {
        super("PAGO_INVALIDO", message);
    }
}