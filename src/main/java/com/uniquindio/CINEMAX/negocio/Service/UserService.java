package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.UserMeDTO;

/**
 * Interfaz de servicio para gestionar la información del usuario en el sistema CINEMAX.
 * Esta interfaz define el método necesario para obtener la información del usuario autenticado (me).
 */
public interface UserService {
    UserMeDTO me(String email);
}