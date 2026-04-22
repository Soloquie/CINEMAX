package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.RolDAO;
import com.uniquindio.CINEMAX.Persistencia.DAO.TokenUsuarioDAO;
import com.uniquindio.CINEMAX.Persistencia.DAO.UsuarioDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoUsuario;
import com.uniquindio.CINEMAX.Persistencia.Entity.TipoToken;
import com.uniquindio.CINEMAX.Seguridad.JwtService;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Exception.*;
import com.uniquindio.CINEMAX.negocio.Model.TokenUsuario;
import com.uniquindio.CINEMAX.negocio.Model.Usuario;
import com.uniquindio.CINEMAX.negocio.Service.AuthService;
import com.uniquindio.CINEMAX.negocio.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Implementación de la interfaz AuthService para gestionar la autenticación y autorización en el sistema CINEMAX.
 * Esta clase maneja el registro, inicio de sesión, verificación de correo electrónico, refresh token
 * y restablecimiento de contraseña.
 * Utiliza DAOs para interactuar con la base de datos y servicios auxiliares para generar tokens JWT y enviar correos electrónicos.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    // DAOs para interactuar con la base de datos
    private final UsuarioDAO usuarioDAO;
    private final RolDAO rolDAO;
    private final TokenUsuarioDAO tokenUsuarioDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    // Valores de configuración para los endpoints y tiempos de expiración de tokens
    @Value("${app.verification.endpoint:http://localhost:4200/api/auth/verify-email}")
    private String verificationEndpoint;

    @Value("${app.verification.token-minutes:1440}")
    private long verificationTokenMinutes;

    @Value("${app.reset-password.endpoint:http://localhost:4200/api/auth/reset-password}")
    private String resetPasswordEndpoint;

    @Value("${app.reset-password.token-minutes:30}")
    private long resetPasswordTokenMinutes;

    @Value("${app.auth.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${app.auth.lock-minutes:15}")
    private int lockMinutes;

    // Hash dummy para mitigar ataques de timing en el login
    private static final String DUMMY_HASH =
            "$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5pWk0pZ5s6m0qOQ8bQ4b9v9l6y9yW";

    /**
     * Implementación del método de registro de usuarios. Valida la información, crea el usuario,
     * genera un token de verificación y envía el correo.
     */
    @Override
    @Transactional
    public MessageResponseDTO register(RegisterRequestDTO request) {
        String email = normalizeEmail(request.email());

        if (usuarioDAO.existePorEmail(email)) {
            throw new EmailYaRegistradoException();
        }

        rolDAO.buscarPorNombre("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("No existe el rol CLIENTE en BD"));

        Usuario nuevo = new Usuario(
                null,
                request.nombre().trim(),
                request.apellido().trim(),
                request.fechaNacimiento(),
                email,
                request.telefono(),
                passwordEncoder.encode(request.password()),
                false,
                EstadoUsuario.ACTIVO,
                null,
                Set.of("CLIENTE"),
                0,
                null
        );

        Usuario guardado = usuarioDAO.guardar(nuevo);

        TokenUsuario token = crearTokenVerificacion(guardado.id());
        TokenUsuario guardadoToken = tokenUsuarioDAO.guardar(token);

        String link = buildVerificationLink(guardadoToken.token());
        emailService.sendVerificationEmail(guardado.email(), guardado.nombre(), link);

        return new MessageResponseDTO("Registro exitoso. Revisa tu correo para verificar la cuenta.");
    }

    /**
     * Implementación del método de inicio de sesión. Valida las credenciales, maneja bloqueos por intentos fallidos,
     * verifica el estado del usuario y genera access token + refresh token si todo es correcto.
     */
    @Override
    @Transactional(noRollbackFor = RuntimeException.class)
    public AuthResponseDTO login(LoginRequestDTO request) {
        String email = normalizeEmail(request.email());
        LocalDateTime now = LocalDateTime.now();

        Usuario usuario = usuarioDAO.buscarPorEmail(email).orElse(null);

        if (usuario == null) {
            passwordEncoder.matches(request.password(), DUMMY_HASH);
            throw new CredencialesInvalidasException();
        }

        if (usuario.estado() == EstadoUsuario.BLOQUEADO || usuario.estado() == EstadoUsuario.INACTIVO) {
            throw new UsuarioNoHabilitadoException("Usuario no habilitado para iniciar sesión");
        }

        if (usuario.bloqueadoHasta() != null && usuario.bloqueadoHasta().isAfter(now)) {
            throw new UsuarioNoHabilitadoException("Cuenta temporalmente bloqueada. Intenta más tarde.");
        }

        if (!usuario.emailVerificado()) {
            throw new UsuarioNoHabilitadoException("Debes verificar tu correo antes de iniciar sesión");
        }

        boolean ok = passwordEncoder.matches(request.password(), usuario.passwordHash());

        if (!ok) {
            int intentos = (usuario.intentosFallidos() == null ? 0 : usuario.intentosFallidos()) + 1;

            LocalDateTime bloqueadoHasta = usuario.bloqueadoHasta();
            if (intentos >= maxFailedAttempts) {
                bloqueadoHasta = now.plusMinutes(lockMinutes);
                intentos = 0; // opcional
            }

            // Actualizamos el usuario con los nuevos intentos y posible bloqueo
            Usuario actualizado = new Usuario(
                    usuario.id(),
                    usuario.nombre(),
                    usuario.apellido(),
                    usuario.fechaNacimiento(),
                    usuario.email(),
                    usuario.telefono(),
                    usuario.passwordHash(),
                    usuario.emailVerificado(),
                    usuario.estado(),
                    usuario.ultimoLoginEn(),
                    usuario.roles(),
                    intentos,
                    bloqueadoHasta
            );
            usuarioDAO.guardar(actualizado);

            throw new CredencialesInvalidasException();
        }

        // Login exitoso: reseteamos intentos y bloqueos, actualizamos último login
        Usuario actualizadoOk = new Usuario(
                usuario.id(),
                usuario.nombre(),
                usuario.apellido(),
                usuario.fechaNacimiento(),
                usuario.email(),
                usuario.telefono(),
                usuario.passwordHash(),
                usuario.emailVerificado(),
                usuario.estado(),
                now,
                usuario.roles(),
                0,
                null
        );
        usuarioDAO.guardar(actualizadoOk);

        // Invalidar refresh tokens activos anteriores del usuario
        tokenUsuarioDAO.buscarActivosPorUsuarioYTipo(usuario.id(), TipoToken.REFRESH_TOKEN)
                .forEach(t -> tokenUsuarioDAO.guardar(new TokenUsuario(
                        t.id(),
                        t.usuarioId(),
                        t.token(),
                        t.tipo(),
                        t.creadoEn(),
                        t.expiraEn(),
                        now
                )));

        String accessToken = jwtService.generateAccessToken(usuario.id(), usuario.email(), usuario.roles());
        String refreshToken = jwtService.generateRefreshToken(usuario.id(), usuario.email());

        LocalDateTime expiraRefresh = now.plusSeconds(jwtService.getRefreshExpiresInSeconds());

        tokenUsuarioDAO.guardar(new TokenUsuario(
                null,
                usuario.id(),
                refreshToken,
                TipoToken.REFRESH_TOKEN,
                now,
                expiraRefresh,
                null
        ));

        // Construimos el DTO de resumen del usuario para incluir en la respuesta
        UserSummaryDTO userSummary = new UserSummaryDTO(
                usuario.id(),
                usuario.nombre(),
                usuario.apellido(),
                usuario.email(),
                usuario.emailVerificado(),
                usuario.estado().name(),
                usuario.roles()
        );

        return new AuthResponseDTO(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessExpiresInSeconds(),
                jwtService.getRefreshExpiresInSeconds(),
                userSummary
        );
    }

    /**
     * Implementación del refresh token. Valida el refresh token recibido,
     * verifica que no esté usado ni expirado, lo rota y genera nuevos tokens.
     */
    @Override
    @Transactional
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String refreshToken = request.refreshToken().trim();
        LocalDateTime now = LocalDateTime.now();

        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new TokenInvalidoException("Refresh token inválido");
        }

        TokenUsuario tokenGuardado = tokenUsuarioDAO.buscarPorToken(refreshToken)
                .orElseThrow(() -> new TokenInvalidoException("Refresh token no reconocido"));

        if (tokenGuardado.usadoEn() != null) {
            throw new TokenInvalidoException("Refresh token ya utilizado");
        }

        if (tokenGuardado.expiraEn().isBefore(now)) {
            throw new TokenExpiradoException("Refresh token expirado");
        }

        if (tokenGuardado.tipo() != TipoToken.REFRESH_TOKEN) {
            throw new TokenInvalidoException("Tipo de token inválido");
        }

        Usuario usuario = usuarioDAO.buscarPorId(tokenGuardado.usuarioId())
                .orElseThrow(UsuarioNoEncontradoException::new);

        // invalidar refresh actual (rotación)
        tokenUsuarioDAO.guardar(new TokenUsuario(
                tokenGuardado.id(),
                tokenGuardado.usuarioId(),
                tokenGuardado.token(),
                tokenGuardado.tipo(),
                tokenGuardado.creadoEn(),
                tokenGuardado.expiraEn(),
                now
        ));

        String newAccessToken = jwtService.generateAccessToken(usuario.id(), usuario.email(), usuario.roles());
        String newRefreshToken = jwtService.generateRefreshToken(usuario.id(), usuario.email());

        LocalDateTime expiraRefresh = now.plusSeconds(jwtService.getRefreshExpiresInSeconds());

        tokenUsuarioDAO.guardar(new TokenUsuario(
                null,
                usuario.id(),
                newRefreshToken,
                TipoToken.REFRESH_TOKEN,
                now,
                expiraRefresh,
                null
        ));

        UserSummaryDTO userSummary = new UserSummaryDTO(
                usuario.id(),
                usuario.nombre(),
                usuario.apellido(),
                usuario.email(),
                usuario.emailVerificado(),
                usuario.estado().name(),
                usuario.roles()
        );

        return new AuthResponseDTO(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtService.getAccessExpiresInSeconds(),
                jwtService.getRefreshExpiresInSeconds(),
                userSummary
        );
    }

    /**
     * Implementación del método de verificación de correo electrónico. Valida el token,
     * verifica que no haya sido usado o expirado.
     *
     * @param request DTO que contiene el token de verificación enviado al correo del usuario.
     * @return MessageResponseDTO con el resultado de la verificación, indicando si fue exitosa
     * o si el correo ya estaba verificado.
     */
    @Override
    @Transactional
    public MessageResponseDTO verifyEmail(VerifyEmailRequestDTO request) {
        String tokenStr = request.token().trim();
        LocalDateTime now = LocalDateTime.now();

        TokenUsuario token = tokenUsuarioDAO.buscarPorToken(tokenStr)
                .orElseThrow(TokenInvalidoException::new);

        if (token.usadoEn() != null) {
            throw new TokenInvalidoException("Este token ya fue utilizado");
        }

        if (token.expiraEn().isBefore(now)) {
            throw new TokenExpiradoException();
        }

        if (token.tipo() != TipoToken.VERIFICAR_EMAIL) {
            throw new TokenInvalidoException("Tipo de token no válido para verificación");
        }

        Usuario usuario = usuarioDAO.buscarPorId(token.usuarioId())
                .orElseThrow(UsuarioNoEncontradoException::new);

        if (usuario.emailVerificado()) {
            return new MessageResponseDTO("Tu correo ya estaba verificado.");
        }

        // Actualizamos el usuario para marcar el correo como verificado
        Usuario actualizado = new Usuario(
                usuario.id(),
                usuario.nombre(),
                usuario.apellido(),
                usuario.fechaNacimiento(),
                usuario.email(),
                usuario.telefono(),
                usuario.passwordHash(),
                true,
                usuario.estado(),
                usuario.ultimoLoginEn(),
                usuario.roles(),
                usuario.intentosFallidos(),
                usuario.bloqueadoHasta()
        );
        usuarioDAO.guardar(actualizado);

        tokenUsuarioDAO.guardar(new TokenUsuario(
                token.id(), token.usuarioId(), token.token(), token.tipo(), token.creadoEn(), token.expiraEn(), now
        ));

        return new MessageResponseDTO("Correo verificado correctamente. Ya puedes iniciar sesión.");
    }

    /**
     * Implementación del método para reenviar el correo de verificación. Busca el usuario por email, verifica su estado
     * y si el correo no está verificado, invalida los tokens anteriores y envía un nuevo correo con un nuevo token de verificación.
     *
     * @param request DTO que contiene el email del usuario para el cual se desea reenviar el correo de verificación.
     * @return MessageResponseDTO indicando que se ha reenviado el correo de verificación.
     */
    @Override
    @Transactional
    public MessageResponseDTO resendVerification(ResendVerificationDTO request) {
        String email = normalizeEmail(request.email());
        Usuario usuario = usuarioDAO.buscarPorEmail(email)
                .orElseThrow(UsuarioNoEncontradoException::new);

        if (usuario.emailVerificado()) {
            return new MessageResponseDTO("Tu correo ya está verificado. Inicia sesión.");
        }

        LocalDateTime now = LocalDateTime.now();
        tokenUsuarioDAO.buscarActivosPorUsuarioYTipo(usuario.id(), TipoToken.VERIFICAR_EMAIL)
                .forEach(t -> tokenUsuarioDAO.guardar(new TokenUsuario(
                        t.id(), t.usuarioId(), t.token(), t.tipo(), t.creadoEn(), t.expiraEn(), now
                )));

        // Creamos un nuevo token de verificación y lo guardamos
        TokenUsuario nuevoToken = crearTokenVerificacion(usuario.id());
        TokenUsuario guardado = tokenUsuarioDAO.guardar(nuevoToken);

        String link = buildVerificationLink(guardado.token());
        emailService.sendVerificationEmail(usuario.email(), usuario.nombre(), link);

        return new MessageResponseDTO("Te reenviamos el correo de verificación.");
    }

    /**
     * Implementación del método para iniciar el proceso de recuperación de contraseña. Busca el usuario por email,
     * y si existe, invalida los tokens de reset anteriores, crea un nuevo token de reset y envía un correo con el enlace.
     *
     * @param request DTO que contiene el email del usuario que ha olvidado su contraseña.
     * @return MessageResponseDTO indicando que se ha enviado un enlace de recuperación si el correo existe.
     */
    @Override
    @Transactional
    public MessageResponseDTO forgotPassword(ForgotPasswordRequestDTO request) {
        String email = normalizeEmail(request.email());

        Usuario usuario = usuarioDAO.buscarPorEmail(email).orElse(null);
        if (usuario == null) {
            return new MessageResponseDTO("Si el correo existe, enviaremos un enlace de recuperación.");
        }

        LocalDateTime now = LocalDateTime.now();

        tokenUsuarioDAO.buscarActivosPorUsuarioYTipo(usuario.id(), TipoToken.RESET_PASSWORD)
                .forEach(t -> tokenUsuarioDAO.guardar(new TokenUsuario(
                        t.id(), t.usuarioId(), t.token(), t.tipo(), t.creadoEn(), t.expiraEn(), now
                )));

        LocalDateTime expira = now.plusMinutes(resetPasswordTokenMinutes);
        String token = UUID.randomUUID().toString().replace("-", "");

        TokenUsuario nuevo = new TokenUsuario(
                null,
                usuario.id(),
                token,
                TipoToken.RESET_PASSWORD,
                null,
                expira,
                null
        );

        TokenUsuario guardado = tokenUsuarioDAO.guardar(nuevo);
        String link = resetPasswordEndpoint + "?token=" + URLEncoder.encode(guardado.token(), StandardCharsets.UTF_8);

        emailService.sendPasswordResetEmail(usuario.email(), usuario.nombre(), link);

        return new MessageResponseDTO("Si el correo existe, enviaremos un enlace de recuperación.");
    }

    /**
     * Implementación del método para restablecer la contraseña. Valida el token, verifica que no haya sido usado o expirado,
     * y si es válido, actualiza la contraseña del usuario y marca el token como usado.
     *
     * @param request DTO que contiene el token de reset y la nueva contraseña.
     * @return MessageResponseDTO indicando que la contraseña se ha actualizado correctamente.
     */
    @Override
    @Transactional
    public MessageResponseDTO resetPassword(ResetPasswordRequestDTO request) {
        String tokenStr = request.token().trim();
        LocalDateTime now = LocalDateTime.now();

        TokenUsuario token = tokenUsuarioDAO.buscarPorToken(tokenStr)
                .orElseThrow(TokenInvalidoException::new);

        if (token.usadoEn() != null) throw new TokenInvalidoException("Token ya utilizado");
        if (token.expiraEn().isBefore(now)) throw new TokenExpiradoException("Token expirado");
        if (token.tipo() != TipoToken.RESET_PASSWORD) throw new TokenInvalidoException("Token no válido para reset");

        Usuario usuario = usuarioDAO.buscarPorId(token.usuarioId())
                .orElseThrow(UsuarioNoEncontradoException::new);

        Usuario actualizado = new Usuario(
                usuario.id(),
                usuario.nombre(),
                usuario.apellido(),
                usuario.fechaNacimiento(),
                usuario.email(),
                usuario.telefono(),
                passwordEncoder.encode(request.newPassword()),
                usuario.emailVerificado(),
                usuario.estado(),
                usuario.ultimoLoginEn(),
                usuario.roles(),
                usuario.intentosFallidos(),
                usuario.bloqueadoHasta()
        );
        usuarioDAO.guardar(actualizado);

        tokenUsuarioDAO.guardar(new TokenUsuario(
                token.id(), token.usuarioId(), token.token(), token.tipo(), token.creadoEn(), token.expiraEn(), now
        ));

        return new MessageResponseDTO("Contraseña actualizada correctamente.");
    }

    /**
     * Método auxiliar para crear un token de verificación de correo electrónico.
     * Genera un token único, establece su tipo y fecha de expiración.
     *
     * @param usuarioId ID del usuario para el cual se crea el token.
     * @return TokenUsuario con la información del token creado.
     */
    private TokenUsuario crearTokenVerificacion(Long usuarioId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expira = now.plus(Duration.ofMinutes(verificationTokenMinutes));
        String token = UUID.randomUUID().toString().replace("-", "");

        return new TokenUsuario(
                null,
                usuarioId,
                token,
                TipoToken.VERIFICAR_EMAIL,
                null,
                expira,
                null
        );
    }

    private String buildVerificationLink(String token) {
        String encoded = URLEncoder.encode(token, StandardCharsets.UTF_8);
        return verificationEndpoint + "?token=" + encoded;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}