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
/* Implementación de la interfaz TokenUsuarioDAO para gestionar las operaciones relacionadas con los tokens de
usuario en el sistema CINEMAX. */
@Repository
@RequiredArgsConstructor
@Transactional
public class TokenUsuarioDAOImpl implements TokenUsuarioDAO {

    private final TokenUsuarioRepository tokenUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final TokenUsuarioMapper tokenUsuarioMapper;
    /** Guarda un nuevo token de usuario en el sistema CINEMAX. Verifica que el usuario asociado al token exista antes de guardarlo.
     * @param tokenUsuario El token de usuario a guardar, que debe incluir el ID del usuario asociado.
     * @return El token de usuario guardado, incluyendo su ID, token, tipo, fecha de creación y fecha de uso (si se ha usado).
     * @throws IllegalArgumentException Si el ID del usuario es nulo o si no existe un usuario con ese ID.
     */
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
    /* Busca un token de usuario por su valor en el sistema CINEMAX.
     * @param token El valor del token a buscar.
     * @return Un Optional que contiene el token de usuario encontrado, o vacío si no se encuentra ningún token con ese valor.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TokenUsuario> buscarPorToken(String token) {
        return tokenUsuarioRepository.findByToken(token).map(tokenUsuarioMapper::toDomain);
    }
    /* Busca todos los tokens de usuario activos (no usados) para un usuario específico y un tipo de token dado en el sistema CINEMAX.
     * @param usuarioId ID del usuario para el cual se buscan los tokens.
     * @param tipo Tipo de token a buscar (por ejemplo, CONFIRMACION_REGISTRO, RECUPERACION_CONTRASENA).
     * @return Una lista de tokens de usuario que coinciden con el usuario y tipo especificados, y que no han sido usados aún.
     */
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