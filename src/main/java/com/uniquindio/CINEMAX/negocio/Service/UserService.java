package com.uniquindio.CINEMAX.negocio.Service;

import com.uniquindio.CINEMAX.negocio.DTO.UserMeDTO;

public interface UserService {
    UserMeDTO me(String email);
}