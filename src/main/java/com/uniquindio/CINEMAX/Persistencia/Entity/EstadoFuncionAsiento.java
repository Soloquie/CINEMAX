package com.uniquindio.CINEMAX.Persistencia.Entity;
/* Enumeración que representa los posibles estados de un asiento en una función en el sistema CINEMAX.
 * Los estados incluyen DISPONIBLE (asiento disponible para reserva), BLOQUEADO (asiento bloqueado temporalmente durante el proceso de reserva),
 * RESERVADO (asiento reservado por un cliente) y VENDIDO (asiento vendido y no disponible para reserva).
  */
public enum EstadoFuncionAsiento {
    DISPONIBLE, BLOQUEADO, RESERVADO, VENDIDO
}