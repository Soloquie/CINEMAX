package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un pago requerido no existe en el sistema CINEMAX. */
public class PagoNoEncontradoException extends NotFoundDomainException {

    public PagoNoEncontradoException() {
        super("PAGO_NO_ENCONTRADO", "El pago no existe.");
    }

    public PagoNoEncontradoException(String message) {
        super("PAGO_NO_ENCONTRADO", message);
    }
}