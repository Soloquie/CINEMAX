package com.uniquindio.CINEMAX.Persistencia.Mapper;

import com.uniquindio.CINEMAX.Persistencia.Entity.TokenUsuarioEntity;
import com.uniquindio.CINEMAX.negocio.Model.TokenUsuario;
import org.springframework.stereotype.Component;
/* Clase de mapeo que se encarga de convertir entre las entidades de token de usuario y los modelos de dominio en el sistema CINEMAX.
 * Proporciona métodos para transformar un TokenUsuarioEntity a un TokenUsuario (modelo de dominio)
 * y un TokenUsuario a un TokenUsuarioEntity  (entidad de persistencia). Es importante destacar que el método toEntity
 * no establece la relación ManyToOne con el usuario,  ya que esto se maneja en el DAO utilizando un usuario "managed".
 *  */
@Component
public class TokenUsuarioMapper {

    public TokenUsuario toDomain(TokenUsuarioEntity e) {
        if (e == null) return null;
        Long usuarioId = (e.getUsuario() == null) ? null : e.getUsuario().getId();

        return new TokenUsuario(
                e.getId(),
                usuarioId,
                e.getToken(),
                e.getTipo(),
                e.getCreadoEn(),
                e.getExpiraEn(),
                e.getUsadoEn()
        );
    }

    /**
     * OJO: No setea usuario (relación ManyToOne). Eso lo hace el DAO con un usuario "managed".
     */
    public TokenUsuarioEntity toEntity(TokenUsuario d) {
        if (d == null) return null;

        return TokenUsuarioEntity.builder()
                .id(d.id())
                .token(d.token())
                .tipo(d.tipo())
                .expiraEn(d.expiraEn())
                .usadoEn(d.usadoEn())
                .build();
    }
}