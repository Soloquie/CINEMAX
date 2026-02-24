package com.uniquindio.CINEMAX.Persistencia.Mapper;

import com.uniquindio.CINEMAX.Persistencia.Entity.UsuarioEntity;
import com.uniquindio.CINEMAX.negocio.Model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity e) {
        if (e == null) return null;

        Set<String> roles = (e.getRoles() == null) ? Set.of() :
                e.getRoles().stream().map(r -> r.getNombre()).collect(Collectors.toSet());

        return new Usuario(
                e.getId(),
                e.getNombre(),
                e.getApellido(),
                e.getFechaNacimiento(),
                e.getEmail(),
                e.getTelefono(),
                e.getPasswordHash(),
                Boolean.TRUE.equals(e.getEmailVerificado()),
                e.getEstado(),
                e.getUltimoLoginEn(),
                roles
        );
    }

    public UsuarioEntity toEntity(Usuario d) {
        if (d == null) return null;

        return UsuarioEntity.builder()
                .id(d.id())
                .nombre(d.nombre())
                .apellido(d.apellido())
                .fechaNacimiento(d.fechaNacimiento())
                .email(d.email())
                .telefono(d.telefono())
                .passwordHash(d.passwordHash())
                .emailVerificado(d.emailVerificado())
                .estado(d.estado())
                .ultimoLoginEn(d.ultimoLoginEn())
                // roles se adjuntan en DAO (managed entities)
                .build();
    }
}