package com.uniquindio.CINEMAX.Persistencia.Entity;
/* Enum que representa el proveedor externo utilizado para procesar el pago.
 * Se define como enum para permitir que en el futuro el sistema CINEMAX pueda soportar
 * otros proveedores sin cambiar la estructura principal del modelo de pagos.
 */
public enum ProveedorPago {
    STRIPE,
    MERCADO_PAGO
}