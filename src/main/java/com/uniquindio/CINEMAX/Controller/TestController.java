package com.uniquindio.CINEMAX.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        // auth.getName() será el email (lo seteamos en el JwtAuthenticationFilter)
        String email = auth.getName();

        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(Map.of(
                "email", email,
                "roles", roles
        ));
    }
}