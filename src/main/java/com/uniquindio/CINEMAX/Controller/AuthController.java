package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * Controlador REST para la autenticación y gestión de usuarios en el sistema CINEMAX.
 * Este controlador proporciona endpoints para el registro, inicio de sesión, verificación de correo electrónico,
 * reenvío de verificación, recuperación de contraseña y restablecimiento de contraseña.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    /**
     * Endpoint para registrar un nuevo usuario en el sistema.
     * @param request Un objeto RegisterRequestDTO que contiene la información necesaria para registrar un nuevo usuario (nombre, correo electrónico, contraseña, etc.).
     * @return Un objeto MessageResponseDTO que contiene un mensaje indicando el resultado del registro, como "Usuario registrado exitosamente" o "Error: El correo electrónico ya está en uso".
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        MessageResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /**
     * Endpoint para iniciar sesión en el sistema.
     * @param request Un objeto LoginRequestDTO que contiene la información necesaria para iniciar sesión (correo electrónico y contraseña).
     * @return Un objeto AuthResponseDTO que contiene un token JWT si las credenciales son correctas, o un mensaje de error si las credenciales son incorrectas.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    /**
     * Endpoint para verificar el correo electrónico de un usuario utilizando un token de verificación.
     * @param token El token de verificación enviado al correo electrónico del usuario durante el registro.
     * @return Un objeto MessageResponseDTO que contiene un mensaje indicando el resultado de la verificación, como "Correo electrónico verificado exitosamente" o "Error: Token de verificación inválido o expirado".
     */
    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponseDTO> verifyEmailByQuery(@RequestParam("token") String token) {
        MessageResponseDTO response = authService.verifyEmail(new VerifyEmailRequestDTO(token));
        return ResponseEntity.ok(response);
    }
    /**
     * Endpoint para verificar el correo electrónico de un usuario utilizando un token de verificación enviado en el cuerpo de la solicitud.
     * @param request Un objeto VerifyEmailRequestDTO que contiene el token de verificación enviado al correo electrónico del usuario durante el registro.
     * @return Un objeto MessageResponseDTO que contiene un mensaje indicando el resultado de la verificación, como "Correo electrónico verificado exitosamente" o "Error: Token de verificación inválido o expirado".
     */
    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponseDTO> verifyEmailByBody(@Valid @RequestBody VerifyEmailRequestDTO request) {
        MessageResponseDTO response = authService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }
    /**
     * Endpoint para reenviar el correo de verificación a un usuario que no ha verificado su correo electrónico.
     * @param request Un objeto ResendVerificationDTO que contiene el correo electrónico del usuario al que se desea reenviar el correo de verificación.
     * @return Un objeto MessageResponseDTO que contiene un mensaje indicando el resultado del reenvío, como "Correo de verificación reenviado exitosamente" o "Error: No se encontró un usuario con ese correo electrónico".
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponseDTO> resendVerification(@Valid @RequestBody ResendVerificationDTO request) {
        MessageResponseDTO response = authService.resendVerification(request);
        return ResponseEntity.ok(response);
    }
    /**
     * Endpoint para iniciar el proceso de recuperación de contraseña para un usuario que ha olvidado su contraseña.
     * @param request Un objeto ForgotPasswordRequestDTO que contiene el correo electrónico del usuario que ha olvidado su contraseña.
     * @return Un objeto MessageResponseDTO que contiene un mensaje indicando el resultado del proceso de recuperación, como "Correo de recuperación enviado exitosamente" o "Error: No se encontró un usuario con ese correo electrónico".
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }
    /**
     * Endpoint para restablecer la contraseña de un usuario utilizando un token de restablecimiento.
     * @param request Un objeto ResetPasswordRequestDTO que contiene el token de restablecimiento enviado al correo electrónico del usuario durante el proceso de recuperación de contraseña, y la nueva contraseña que el usuario desea establecer.
     * @return Un objeto MessageResponseDTO que contiene un mensaje indicando el resultado del restablecimiento, como "Contraseña restablecida exitosamente" o "Error: Token de restablecimiento inválido o expirado".
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDTO> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    /**
     * Endpoint para refrescar el token de JWT del login
     * @return Un objeto MessageResponseDTO que contiene un mensaje indicando el resultado del token refrescado.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}