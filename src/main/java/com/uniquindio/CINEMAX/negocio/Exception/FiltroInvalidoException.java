package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que un filtro o parámetro de consulta no cumple con las reglas definidas en el sistema CINEMAX. */
public class FiltroInvalidoException extends ValidationDomainException {

    public FiltroInvalidoException(String message) {
        super("FILTRO_INVALIDO", message);
    }
}