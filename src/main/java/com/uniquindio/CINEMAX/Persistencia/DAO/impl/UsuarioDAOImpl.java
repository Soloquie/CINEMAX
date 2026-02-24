package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.UsuarioDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.RolEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.UsuarioEntity;
import com.uniquindio.CINEMAX.Persistencia.Mapper.UsuarioMapper;
import com.uniquindio.CINEMAX.Persistencia.Repository.RolRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.UsuarioRepository;
import com.uniquindio.CINEMAX.negocio.Model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional
public class UsuarioDAOImpl implements UsuarioDAO {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = usuarioMapper.toEntity(usuario);

        // Adjuntar roles como entidades "managed" (evita detached entity issues)
        Set<String> rolesNombres = (usuario.roles() == null) ? Set.of() : usuario.roles();
        if (!rolesNombres.isEmpty()) {
            Set<RolEntity> roles = rolesNombres.stream()
                    .map(nombre -> rolRepository.findByNombre(nombre)
                            .orElseThrow(() -> new IllegalArgumentException("Rol no existe: " + nombre)))
                    .collect(Collectors.toSet());
            entity.setRoles(roles);
        }

        UsuarioEntity saved = usuarioRepository.save(entity);
        return usuarioMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(usuarioMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(usuarioMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}