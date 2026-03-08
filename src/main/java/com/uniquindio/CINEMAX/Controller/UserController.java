package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.UserMeDTO;
import com.uniquindio.CINEMAX.negocio.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Requiere JWT
    @GetMapping("/me")
    public UserMeDTO me(Authentication auth) {
        return userService.me(auth.getName());
    }
}