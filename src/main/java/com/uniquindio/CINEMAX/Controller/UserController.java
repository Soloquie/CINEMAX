package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.UserMeDTO;
import com.uniquindio.CINEMAX.negocio.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
/**
 * Controlador REST para la gestión de usuarios en el sistema CINEMAX.
 * Este controlador proporciona un endpoint para que los usuarios puedan obtener su información personal (perfil) utilizando su token JWT.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    /* Endpoint para obtener la información personal del usuario autenticado (perfil).
     * Este endpoint requiere que el usuario esté autenticado mediante JWT, y utiliza la información del token para identificar al usuario y devolver su perfil.
     * @param auth El objeto Authentication que contiene la información del usuario autenticado, incluyendo su nombre de usuario.
     * @return Un objeto UserMeDTO que contiene la información del perfil del usuario autenticado, como su nombre, correo electrónico, roles, etc.
     */
    @GetMapping("/me")
    public UserMeDTO me(Authentication auth) {
        return userService.me(auth.getName());
    }
}