package com.uniquindio.CINEMAX.Persistencia.Mapper;

import com.uniquindio.CINEMAX.Persistencia.Entity.UsuarioEntity;
import com.uniquindio.CINEMAX.negocio.Model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
/* Clase de mapeo que se encarga de convertir entre las entidades de usuario y los modelos de dominio en el sistema CINEMAX.
 * Proporciona métodos para transformar un UsuarioEntity a un Usuario (modelo de dominio) y un Usuario a un UsuarioEntity
 * (entidad de persistencia). Es importante destacar que el método toEntity no establece la relación ManyToMany con los roles,
 * ya que esto se maneja en el DAO utilizando roles "managed".
 *  */
@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity e) {
        if (e == null) return null;

        Integer intentos = e.getIntentosFallidos() == null ? 0 : e.getIntentosFallidos();

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
                roles,
                intentos,
                e.getBloqueadoHasta()
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
                .intentosFallidos(d.intentosFallidos() == null ? 0 : d.intentosFallidos())
                .bloqueadoHasta(d.bloqueadoHasta())
                // roles se adjuntan en DAO (managed entities)
                .build();
    }
}