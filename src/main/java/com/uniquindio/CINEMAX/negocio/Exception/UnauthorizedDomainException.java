package com.uniquindio.CINEMAX.negocio.Exception;

import org.springframework.http.HttpStatus;
/* Excepción base abstracta para representar errores de autenticación o autorización de dominio en el sistema CINEMAX.
 * Se utiliza cuando un usuario no está habilitado para ejecutar una operación específica.
 */
public abstract class UnauthorizedDomainException extends DomainException {

    protected UnauthorizedDomainException(String code, String message) {
        super(code, message, HttpStatus.UNAUTHORIZED);
    }
}