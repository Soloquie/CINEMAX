package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que una función requerida no existe en el sistema CINEMAX. */
public class FuncionNoEncontradaException extends NotFoundDomainException {

    public FuncionNoEncontradaException() {
        super("FUNCION_NO_ENCONTRADA", "La función no existe.");
    }

    public FuncionNoEncontradaException(String message) {
        super("FUNCION_NO_ENCONTRADA", message);
    }
}