package com.uniquindio.CINEMAX.Persistencia.Mapper;

import com.uniquindio.CINEMAX.Persistencia.Entity.GeneroEntity;
import com.uniquindio.CINEMAX.negocio.DTO.GeneroDTO;
import com.uniquindio.CINEMAX.negocio.Model.Genero;
import org.springframework.stereotype.Component;
/* Clase de mapeo que se encarga de convertir entre las entidades de género, los modelos de dominio y los DTOs en el sistema CINEMAX.
 * Proporciona métodos para transformar un GeneroEntity a un Genero (modelo de dominio) y un Genero a un GeneroDTO
 * (objeto de transferencia de datos).
  */
@Component
public class GeneroMapper {
    public Genero toDomain(GeneroEntity e){ return new Genero(e.getId(), e.getNombre()); }
    public GeneroDTO toDTO(Genero g){ return new GeneroDTO(g.id(), g.nombre()); }
}