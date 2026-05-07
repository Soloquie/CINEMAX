package com.uniquindio.CINEMAX.Persistencia.Entity;
/* Enum que representa los posibles estados de un pago dentro del sistema CINEMAX.
 * Permite controlar el ciclo de vida del pago desde que se inicia en el proveedor externo,
 * hasta que se confirma internamente la venta o se detecta un error en el proceso.
 */
public enum EstadoPago {
    PENDIENTE,
    APROBADO,
    RECHAZADO,
    ERROR,
    CONFIRMADO,
    FALLIDO_CONFIRMACION
}