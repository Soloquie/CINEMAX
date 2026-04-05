package com.uniquindio.CINEMAX.config;

import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoUsuario;
import com.uniquindio.CINEMAX.Persistencia.Entity.RolEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.UsuarioEntity;
import com.uniquindio.CINEMAX.Persistencia.Repository.RolRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Value("${app.seed.admin.email:admin@cinemax.com}")
    private String adminEmail;

    @Value("${app.seed.admin.password:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) return;

        // 1) Roles
        RolEntity adminRole = rolRepository.findByNombre("ADMIN")
                .orElseGet(() -> rolRepository.save(RolEntity.builder()
                        .nombre("ADMIN")
                        .descripcion("Administrador del sistema")
                        .build()));

        RolEntity clientRole = rolRepository.findByNombre("CLIENTE")
                .orElseGet(() -> rolRepository.save(RolEntity.builder()
                        .nombre("CLIENTE")
                        .descripcion("Usuario cliente del cine")
                        .build()));

        // 2) Admin user
        if (usuarioRepository.existsByEmail(adminEmail.trim().toLowerCase())) return;

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException("Seed habilitado pero app.seed.admin.password está vacío.");
        }

        UsuarioEntity admin = UsuarioEntity.builder()
                .nombre("Admin")
                .apellido("Cinemax")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .email(adminEmail.trim().toLowerCase())
                .telefono("0000000000")
                .passwordHash(passwordEncoder.encode(adminPassword))
                .emailVerificado(true)
                .estado(EstadoUsuario.ACTIVO)
                .intentosFallidos(0)
                .bloqueadoHasta(null)
                .roles(Set.of(adminRole, clientRole))
                .build();

        usuarioRepository.save(admin);
    }
}