package com.uniquindio.CINEMAX.negocio.Exception;
/* Excepción de dominio para representar el caso en que se intenta agregar o mantener productos de confitería sin una boleta asociada en el sistema CINEMAX. */
public class ConfiteriaNoPermitidaSinBoletaException extends ConflictDomainException {

    public ConfiteriaNoPermitidaSinBoletaException() {
        super("CONFITERIA_NO_PERMITIDA_SIN_BOLETA", "Debes seleccionar al menos una boleta antes de agregar productos de confitería.");
    }

    public ConfiteriaNoPermitidaSinBoletaException(String message) {
        super("CONFITERIA_NO_PERMITIDA_SIN_BOLETA", message);
    }
}