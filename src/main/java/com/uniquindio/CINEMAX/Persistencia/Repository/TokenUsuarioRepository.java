package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.TipoToken;
import com.uniquindio.CINEMAX.Persistencia.Entity.TokenUsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas con
* los tokens de usuario en el sistema CINEMAX. Proporciona métodos personalizados para buscar un token por su valor,
* así como para obtener una lista de tokens asociados a un usuario específico,  filtrados por tipo y estado de uso. Esto
*  facilita la gestión de los tokens de autenticación y recuperación de contraseña, permitiendo realizar consultas
*  específicas según las necesidades del negocio.
  */
public interface TokenUsuarioRepository extends JpaRepository<TokenUsuarioEntity, Long> {

    Optional<TokenUsuarioEntity> findByToken(String token);

    List<TokenUsuarioEntity> findByUsuarioIdAndTipoAndUsadoEnIsNull(Long usuarioId, TipoToken tipo);
}