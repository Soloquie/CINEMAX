package com.uniquindio.CINEMAX.Persistencia.Mapper;

import com.uniquindio.CINEMAX.Persistencia.Entity.GeneroEntity;
import com.uniquindio.CINEMAX.negocio.DTO.GeneroDTO;
import com.uniquindio.CINEMAX.negocio.Model.Genero;
import org.springframework.stereotype.Component;

@Component
public class GeneroMapper {
    public Genero toDomain(GeneroEntity e){ return new Genero(e.getId(), e.getNombre()); }
    public GeneroDTO toDTO(Genero g){ return new GeneroDTO(g.id(), g.nombre()); }
}