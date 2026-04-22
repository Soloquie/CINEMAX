package com.uniquindio.CINEMAX.negocio.Exception;

import org.springframework.http.HttpStatus;
/* Excepción base abstracta para representar errores de validación de dominio en el sistema CINEMAX.
 * Se utiliza cuando los datos de entrada o las reglas funcionales no cumplen con las condiciones esperadas.
 */
public abstract class ValidationDomainException extends DomainException {

    protected ValidationDomainException(String code, String message) {
        super(code, message, HttpStatus.BAD_REQUEST);
    }
}