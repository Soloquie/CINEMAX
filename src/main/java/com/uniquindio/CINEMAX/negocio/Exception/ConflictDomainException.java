package com.uniquindio.CINEMAX.negocio.Exception;

import org.springframework.http.HttpStatus;
/* Excepción base abstracta para representar conflictos de dominio en el sistema CINEMAX.
 * Se utiliza cuando una operación no puede completarse debido a un conflicto con el estado actual del recurso.
 */
public abstract class ConflictDomainException extends DomainException {

    protected ConflictDomainException(String code, String message) {
        super(code, message, HttpStatus.CONFLICT);
    }
}