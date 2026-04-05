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
/* Implementación de la interfaz UsuarioDAO para gestionar las operaciones relacionadas con los usuarios en el sistema CINEMAX. */
@Repository
@RequiredArgsConstructor
@Transactional
public class UsuarioDAOImpl implements UsuarioDAO {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    /** Guarda un nuevo usuario en el sistema CINEMAX. Si el usuario ya existe (tiene un ID), se actualiza; de lo contrario,
     se crea un nuevo usuario. También se asocian los roles correspondientes al usuario.
     * @param usuario Objeto Usuario que contiene la información del usuario a guardar o actualizar,
     * incluyendo su email, contraseña y roles.
     * @return El usuario guardado o actualizado, incluyendo su ID y los roles asociados.
     * @throws IllegalArgumentException Si se intenta actualizar un usuario que no existe o si se asigna un rol que no existe.
     */
    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = usuarioMapper.toEntity(usuario);
        // Si el usuario tiene un ID, intentamos cargar la entidad existente para actualizarla
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
    /* Busca un usuario por su email en el sistema CINEMAX.
     * @param email El email del usuario a buscar.
     * @return Un Optional que contiene el usuario encontrado, o vacío si no se encuentra ningún usuario con ese email.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(usuarioMapper::toDomain);
    }
    /* Busca un usuario por su ID en el sistema CINEMAX.
     * @param id ID del usuario a buscar.
     * @return Un Optional que contiene el usuario encontrado, o vacío si no se encuentra ningún usuario con ese ID.
     */
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