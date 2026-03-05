package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 1) Registro (envía correo con token)
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        MessageResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2) Login (devuelve JWT)
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 3A) Verificar correo desde LINK (recomendado en email)
    // Ej: http://localhost:8080/api/auth/verify-email?token=xxxx
    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponseDTO> verifyEmailByQuery(@RequestParam("token") String token) {
        MessageResponseDTO response = authService.verifyEmail(new VerifyEmailRequestDTO(token));
        return ResponseEntity.ok(response);
    }

    // 3B) Verificar correo desde JSON (opcional)
    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponseDTO> verifyEmailByBody(@Valid @RequestBody VerifyEmailRequestDTO request) {
        MessageResponseDTO response = authService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    // 4) Reenviar verificación
    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponseDTO> resendVerification(@Valid @RequestBody ResendVerificationDTO request) {
        MessageResponseDTO response = authService.resendVerification(request);
        return ResponseEntity.ok(response);
    }
}