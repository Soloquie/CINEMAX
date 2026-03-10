package com.uniquindio.CINEMAX.Persistencia.Mapper;

import com.uniquindio.CINEMAX.Persistencia.Entity.PeliculaEntity;
import com.uniquindio.CINEMAX.negocio.DTO.PeliculaResponseDTO;
import com.uniquindio.CINEMAX.negocio.Model.Pelicula;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
/* Clase de mapeo que se encarga de convertir entre las entidades de película, los modelos de dominio y los DTOs en el sistema CINEMAX.
 * Proporciona métodos para transformar un PeliculaEntity a un Pelicula (modelo de dominio) y un Pelicula a un PeliculaResponseDTO
 * (objeto de transferencia de datos).
  */
@Component
public class PeliculaMapper {

    private final GeneroMapper generoMapper;

    public PeliculaMapper(GeneroMapper generoMapper) {
        this.generoMapper = generoMapper;
    }

    public Pelicula toDomain(PeliculaEntity e){
        return new Pelicula(
                e.getId(),
                e.getTitulo(),
                e.getSinopsis(),
                e.getDuracionMin(),
                e.getClasificacion(),
                e.getFechaEstreno(),
                e.getPosterUrl(),
                e.getActiva(),
                e.getGeneros().stream().map(generoMapper::toDomain).collect(Collectors.toSet())
        );
    }

    public PeliculaResponseDTO toDTO(Pelicula p){
        return new PeliculaResponseDTO(
                p.id(), p.titulo(), p.sinopsis(), p.duracionMin(), p.clasificacion(),
                p.fechaEstreno(), p.posterUrl(), p.activa(),
                p.generos().stream().map(generoMapper::toDTO).collect(Collectors.toSet())
        );
    }
}