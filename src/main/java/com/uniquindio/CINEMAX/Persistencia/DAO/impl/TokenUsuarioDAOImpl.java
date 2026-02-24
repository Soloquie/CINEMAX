package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.TokenUsuarioDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.TipoToken;
import com.uniquindio.CINEMAX.Persistencia.Entity.TokenUsuarioEntity;
import com.uniquindio.CINEMAX.Persistencia.Mapper.TokenUsuarioMapper;
import com.uniquindio.CINEMAX.Persistencia.Repository.TokenUsuarioRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.UsuarioRepository;
import com.uniquindio.CINEMAX.negocio.Model.TokenUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class TokenUsuarioDAOImpl implements TokenUsuarioDAO {

    private final TokenUsuarioRepository tokenUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final TokenUsuarioMapper tokenUsuarioMapper;

    @Override
    public TokenUsuario guardar(TokenUsuario tokenUsuario) {
        if (tokenUsuario.usuarioId() == null) {
            throw new IllegalArgumentException("usuarioId es obligatorio para guardar un token");
        }

        TokenUsuarioEntity entity = tokenUsuarioMapper.toEntity(tokenUsuario);

        // Adjuntar usuario managed (evita detached entity)
        entity.setUsuario(usuarioRepository.getReferenceById(tokenUsuario.usuarioId()));

        TokenUsuarioEntity saved = tokenUsuarioRepository.save(entity);
        return tokenUsuarioMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TokenUsuario> buscarPorToken(String token) {
        return tokenUsuarioRepository.findByToken(token).map(tokenUsuarioMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TokenUsuario> buscarActivosPorUsuarioYTipo(Long usuarioId, TipoToken tipo) {
        return tokenUsuarioRepository
                .findByUsuarioIdAndTipoAndUsadoEnIsNull(usuarioId, tipo)
                .stream()
                .map(tokenUsuarioMapper::toDomain)
                .toList();
    }
}