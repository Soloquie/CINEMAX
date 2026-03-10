package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas con
* los roles de usuario en el sistema CINEMAX. Proporciona un método personalizado para buscar un rol por su nombre,
* lo cual es útil para asignar roles a los usuarios y gestionar sus permisos dentro del sistema.
  */
public interface RolRepository extends JpaRepository<RolEntity, Long> {

    Optional<RolEntity> findByNombre(String nombre); // "ADMIN", "CLIENTE"
}