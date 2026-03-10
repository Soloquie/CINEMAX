package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.Repository.UsuarioRepository;
import com.uniquindio.CINEMAX.negocio.DTO.UserMeDTO;
import com.uniquindio.CINEMAX.negocio.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;
/**
 * Implementación de la interfaz UserService para gestionar las operaciones relacionadas con los usuarios en el sistema CINEMAX.
 * Esta clase utiliza un repositorio para interactuar con la base de datos y realizar las operaciones necesarias.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Implementación del método para obtener la información del usuario actual (me) utilizando su correo electrónico.
     * @param email Correo electrónico del usuario para el cual se desea obtener la información.
     * @return DTO que representa la información del usuario actual, incluyendo su ID, nombre, apellido,
     * fecha de nacimiento, correo electrónico, teléfono, estado de verificación de correo electrónico,
     * estado de la cuenta y roles asociados.
     */
    @Override
    public UserMeDTO me(String email) {
        var u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe."));

        Set<String> roles = (u.getRoles() == null) ? Set.of()
                : u.getRoles().stream().map(r -> r.getNombre()).collect(Collectors.toSet());

        return new UserMeDTO(
                u.getId(),
                u.getNombre(),
                u.getApellido(),
                u.getFechaNacimiento(),
                u.getEmail(),
                u.getTelefono(),
                Boolean.TRUE.equals(u.getEmailVerificado()),
                u.getEstado().name(),
                roles
        );
    }
}