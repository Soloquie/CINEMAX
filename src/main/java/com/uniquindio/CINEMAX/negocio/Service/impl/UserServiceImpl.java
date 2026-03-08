package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.Repository.UsuarioRepository;
import com.uniquindio.CINEMAX.negocio.DTO.UserMeDTO;
import com.uniquindio.CINEMAX.negocio.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;

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