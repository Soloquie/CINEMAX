package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.RolDAO;
import com.uniquindio.CINEMAX.Persistencia.DAO.TokenUsuarioDAO;
import com.uniquindio.CINEMAX.Persistencia.DAO.UsuarioDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoUsuario;
import com.uniquindio.CINEMAX.Persistencia.Entity.TipoToken;
import com.uniquindio.CINEMAX.Seguridad.JwtService;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Exception.CredencialesInvalidasException;
import com.uniquindio.CINEMAX.negocio.Exception.TokenExpiradoException;
import com.uniquindio.CINEMAX.negocio.Exception.TokenInvalidoException;
import com.uniquindio.CINEMAX.negocio.Model.Rol;
import com.uniquindio.CINEMAX.negocio.Model.TokenUsuario;
import com.uniquindio.CINEMAX.negocio.Model.Usuario;
import com.uniquindio.CINEMAX.negocio.Service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private RolDAO rolDAO;

    @Mock
    private TokenUsuarioDAO tokenUsuarioDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginUsuarioNoExiste() {

        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "123456");

        when(usuarioDAO.buscarPorEmail("test@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class, () -> {
            authService.login(request);
        });

        verify(usuarioDAO, times(1)).buscarPorEmail("test@test.com");
    }

    @Test
    void loginPasswordIncorrecto() {

        Usuario usuario = new Usuario(
                1L,
                "Juan",
                "Perez",
                LocalDate.now(),
                "test@test.com",
                "3001234567",
                "hash",
                true,
                EstadoUsuario.ACTIVO,
                null,
                Set.of("CLIENTE"),
                0,
                null
        );

        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "wrong");

        when(usuarioDAO.buscarPorEmail("test@test.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class, () -> {
            authService.login(request);
        });

        verify(usuarioDAO).guardar(any());
    }

    @Test
    void loginExitoso() {

        Usuario usuario = new Usuario(
                1L,
                "Juan",
                "Perez",
                LocalDate.now(),
                "test@test.com",
                "3001234567",
                "hash",
                true,
                EstadoUsuario.ACTIVO,
                null,
                Set.of("CLIENTE"),
                0,
                null
        );

        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "123");

        when(usuarioDAO.buscarPorEmail("test@test.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("123", "hash")).thenReturn(true);

        when(tokenUsuarioDAO.buscarActivosPorUsuarioYTipo(1L, TipoToken.REFRESH_TOKEN))
                .thenReturn(List.of());

        when(jwtService.generateAccessToken(anyLong(), anyString(), anySet()))
                .thenReturn("fake-access-jwt");

        when(jwtService.generateRefreshToken(anyLong(), anyString()))
                .thenReturn("fake-refresh-jwt");

        when(jwtService.getAccessExpiresInSeconds()).thenReturn(900L);
        when(jwtService.getRefreshExpiresInSeconds()).thenReturn(604800L);

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-access-jwt", response.accessToken());
        assertEquals("fake-refresh-jwt", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(900L, response.expiresIn());
        assertEquals(604800L, response.refreshExpiresIn());
    }

    @Test
    void registerExitoso() {

        RegisterRequestDTO request = new RegisterRequestDTO(
                "Juan",
                "Perez",
                LocalDate.of(2000, 1, 1),
                "test@test.com",
                "3001234567",
                "12345678"
        );

        when(usuarioDAO.existePorEmail("test@test.com")).thenReturn(false);

        Rol rolMock = mock(Rol.class);

        when(rolDAO.buscarPorNombre("CLIENTE"))
                .thenReturn(Optional.of(rolMock));

        when(passwordEncoder.encode(any())).thenReturn("hash");

        Usuario usuarioGuardado = new Usuario(
                1L, "Juan", "Perez",
                LocalDate.of(2000, 1, 1),
                "test@test.com", "300",
                "hash", false,
                EstadoUsuario.ACTIVO,
                null,
                Set.of("CLIENTE"),
                0, null
        );

        when(usuarioDAO.guardar(any())).thenReturn(usuarioGuardado);
        when(tokenUsuarioDAO.guardar(any())).thenAnswer(i -> i.getArgument(0));

        assertNotNull(authService.register(request));

        verify(emailService, times(1))
                .sendVerificationEmail(any(), any(), any());
    }

    @Test
    void verifyEmailExitoso() {

        String tokenStr = "token123";

        VerifyEmailRequestDTO request = new VerifyEmailRequestDTO(tokenStr);

        TokenUsuario token = new TokenUsuario(
                1L,
                1L,
                tokenStr,
                TipoToken.VERIFICAR_EMAIL,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                null
        );

        Usuario usuario = new Usuario(
                1L, "Juan", "Perez",
                LocalDate.now(),
                "test@test.com",
                "300",
                "hash",
                false,
                EstadoUsuario.ACTIVO,
                null,
                Set.of("CLIENTE"),
                0,
                null
        );

        when(tokenUsuarioDAO.buscarPorToken(tokenStr)).thenReturn(Optional.of(token));
        when(usuarioDAO.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        assertNotNull(authService.verifyEmail(request));

        verify(usuarioDAO).guardar(any());
    }

    @Test
    void verifyEmailTokenExpirado() {
        String tokenStr = "tokenExpirado";

        VerifyEmailRequestDTO request = new VerifyEmailRequestDTO(tokenStr);

        TokenUsuario token = new TokenUsuario(
                1L,
                1L,
                tokenStr,
                TipoToken.VERIFICAR_EMAIL,
                LocalDateTime.now().minusMinutes(20),
                LocalDateTime.now().minusMinutes(10),
                null
        );

        when(tokenUsuarioDAO.buscarPorToken(tokenStr)).thenReturn(Optional.of(token));

        assertThrows(TokenExpiradoException.class, () -> authService.verifyEmail(request));
    }

    @Test
    void forgotPasswordGeneraToken() {

        ForgotPasswordRequestDTO request =
                new ForgotPasswordRequestDTO("test@test.com");

        Usuario usuario = new Usuario(
                1L, "Juan", "Perez",
                LocalDate.now(),
                "test@test.com",
                "300",
                "hash",
                true,
                EstadoUsuario.ACTIVO,
                null,
                Set.of("CLIENTE"),
                0,
                null
        );

        when(usuarioDAO.buscarPorEmail("test@test.com"))
                .thenReturn(Optional.of(usuario));

        when(tokenUsuarioDAO.buscarActivosPorUsuarioYTipo(any(), any()))
                .thenReturn(List.of());

        when(tokenUsuarioDAO.guardar(any()))
                .thenAnswer(i -> i.getArgument(0));

        assertNotNull(authService.forgotPassword(request));

        verify(emailService).sendPasswordResetEmail(any(), any(), any());
    }

    @Test
    void resetPasswordExitoso() {

        String tokenStr = "tokenReset";

        ResetPasswordRequestDTO request =
                new ResetPasswordRequestDTO(tokenStr, "nueva123");

        TokenUsuario token = new TokenUsuario(
                1L,
                1L,
                tokenStr,
                TipoToken.RESET_PASSWORD,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                null
        );

        Usuario usuario = new Usuario(
                1L, "Juan", "Perez",
                LocalDate.now(),
                "test@test.com",
                "300",
                "hash",
                true,
                EstadoUsuario.ACTIVO,
                null,
                Set.of("CLIENTE"),
                0,
                null
        );

        when(tokenUsuarioDAO.buscarPorToken(tokenStr))
                .thenReturn(Optional.of(token));

        when(usuarioDAO.buscarPorId(1L))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.encode(any()))
                .thenReturn("nuevoHash");

        assertNotNull(authService.resetPassword(request));

        verify(usuarioDAO).guardar(any());
    }

    @Test
    void resetPasswordTokenInvalido() {
        String tokenStr = "tokenInvalido";

        ResetPasswordRequestDTO request =
                new ResetPasswordRequestDTO(tokenStr, "nueva123");

        when(tokenUsuarioDAO.buscarPorToken(tokenStr))
                .thenReturn(Optional.empty());

        assertThrows(TokenInvalidoException.class, () -> authService.resetPassword(request));
    }

    @Test
    void resendVerificationEnviaCorreo() {

        ResendVerificationDTO request =
                new ResendVerificationDTO("test@test.com");

        Usuario usuario = new Usuario(
                1L, "Juan", "Perez",
                LocalDate.now(),
                "test@test.com",
                "300",
                "hash",
                false,
                EstadoUsuario.ACTIVO,
                null,
                Set.of("CLIENTE"),
                0,
                null
        );

        when(usuarioDAO.buscarPorEmail("test@test.com"))
                .thenReturn(Optional.of(usuario));

        when(tokenUsuarioDAO.buscarActivosPorUsuarioYTipo(any(), any()))
                .thenReturn(List.of());

        when(tokenUsuarioDAO.guardar(any()))
                .thenAnswer(i -> i.getArgument(0));

        assertNotNull(authService.resendVerification(request));

        verify(emailService).sendVerificationEmail(any(), any(), any());
    }
}