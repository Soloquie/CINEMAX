package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoToken;
import com.uniquindio.CINEMAX.Persistencia.Entity.TokenUsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenUsuarioRepository extends JpaRepository<TokenUsuarioEntity, Long> {

    Optional<TokenUsuarioEntity> findByToken(String token);

    List<TokenUsuarioEntity> findByUsuarioIdAndTipoAndUsadoEnIsNull(Long usuarioId, TipoToken tipo);
}