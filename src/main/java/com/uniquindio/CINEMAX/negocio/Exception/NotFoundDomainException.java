package com.uniquindio.CINEMAX.negocio.Exception;

import org.springframework.http.HttpStatus;
/* Excepción base abstracta para representar recursos de dominio no encontrados en el sistema CINEMAX.
 * Se utiliza cuando una entidad requerida no existe o no puede ser localizada.
 */
public abstract class NotFoundDomainException extends DomainException {

    protected NotFoundDomainException(String code, String message) {
        super(code, message, HttpStatus.NOT_FOUND);
    }
}