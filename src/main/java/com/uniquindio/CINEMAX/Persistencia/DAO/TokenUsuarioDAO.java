package com.uniquindio.CINEMAX.Persistencia.DAO;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoToken;
import com.uniquindio.CINEMAX.negocio.Model.TokenUsuario;

import java.util.List;
import java.util.Optional;

public interface TokenUsuarioDAO {
    TokenUsuario guardar(TokenUsuario tokenUsuario);
    Optional<TokenUsuario> buscarPorToken(String token);
    List<TokenUsuario> buscarActivosPorUsuarioYTipo(Long usuarioId, TipoToken tipo);
}