package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.CarritoEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/* Interfaz de repositorio que extiende JpaRepository para gestionar las operaciones de persistencia relacionadas
* con los carritos de compra en el sistema CINEMAX. Proporciona un método personalizado para buscar el carrito
* más reciente de un usuario específico que esté en un estado determinado, ordenado por la fecha de actualización en
* orden descendente. Esto permite obtener el carrito activo más reciente para un usuario dado, lo cual es útil
*  para gestionar las compras en curso. */
public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {
    Optional<CarritoEntity> findFirstByUsuarioIdAndEstadoOrderByActualizadoEnDesc(Long usuarioId, EstadoCarrito estado);
}