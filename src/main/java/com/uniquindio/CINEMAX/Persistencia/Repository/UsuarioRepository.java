package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas con
* los usuarios en el sistema CINEMAX. Proporciona métodos personalizados para buscar un usuario por su correo electrónico
* y verificar la existencia de un usuario con un correo electrónico específico. Esto facilita la gestión de los usuarios
*  registrados en el sistema, permitiendo realizar consultas específicas según las necesidades del negocio,
*  como autenticación y validación de datos.
  */
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}