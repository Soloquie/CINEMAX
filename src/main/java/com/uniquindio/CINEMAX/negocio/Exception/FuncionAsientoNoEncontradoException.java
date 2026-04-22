package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que la relación entre una función y un asiento no existe en el sistema CINEMAX. */
public class FuncionAsientoNoEncontradoException extends NotFoundDomainException {

    public FuncionAsientoNoEncontradoException() {
        super("FUNCION_ASIENTO_NO_ENCONTRADO", "La relación función-asiento no existe.");
    }

    public FuncionAsientoNoEncontradoException(String message) {
        super("FUNCION_ASIENTO_NO_ENCONTRADO", message);
    }
}