package com.uniquindio.CINEMAX.Persistencia.Entity;
/* Enum que representa los posibles estados de una venta dentro del sistema CINEMAX.
 * Una venta confirmada indica que el pago fue aprobado y que los asientos/productos
 * fueron registrados correctamente dentro de una transacción de base de datos.
 */
public enum EstadoVenta {
    CONFIRMADA,
    CANCELADA
}