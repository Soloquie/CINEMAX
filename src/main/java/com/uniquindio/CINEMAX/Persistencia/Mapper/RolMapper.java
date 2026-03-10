package com.uniquindio.CINEMAX.Persistencia.Mapper;

import com.uniquindio.CINEMAX.Persistencia.Entity.RolEntity;
import com.uniquindio.CINEMAX.negocio.Model.Rol;
import org.springframework.stereotype.Component;
/* Clase de mapeo que se encarga de convertir entre las entidades de rol y los modelos de dominio en el sistema CINEMAX.
 * Proporciona métodos para transformar un RolEntity a un Rol (modelo de dominio) y un Rol a un RolEntity
 * (entidad de persistencia).
 *  */
@Component
public class RolMapper {

    public Rol toDomain(RolEntity e) {
        if (e == null) return null;
        return new Rol(e.getId(), e.getNombre(), e.getDescripcion());
    }

    public RolEntity toEntity(Rol d) {
        if (d == null) return null;
        return RolEntity.builder()
                .id(d.id())
                .nombre(d.nombre())
                .descripcion(d.descripcion())
                .build();
    }
}