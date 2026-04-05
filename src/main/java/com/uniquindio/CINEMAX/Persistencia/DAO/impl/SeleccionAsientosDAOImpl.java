package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.SeleccionAsientosDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoFuncionAsiento;
import com.uniquindio.CINEMAX.Persistencia.Entity.FuncionAsientoEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.UsuarioEntity;
import com.uniquindio.CINEMAX.Persistencia.Repository.FuncionAsientoRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.FuncionRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.UsuarioRepository;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
/* Implementación de la interfaz SeleccionAsientosDAO para gestionar las operaciones relacionadas con la selección
 de asientos en el sistema CINEMAX. */
@Repository
@RequiredArgsConstructor
@Transactional
public class SeleccionAsientosDAOImpl implements SeleccionAsientosDAO {

    private final FuncionRepository funcionRepository;
    private final FuncionAsientoRepository funcionAsientoRepository;
    private final UsuarioRepository usuarioRepository;
    // Tiempo en minutos que un asiento queda retenido (hold) antes de volver a estar disponible automáticamente
    @Value("${app.seat-hold.minutes:10}")
    private int holdMinutes;
    /** Lista los asientos disponibles para una función específica en el sistema CINEMAX.
     * Verifica que la función exista antes de listar los asientos.
     * @param funcionId ID de la función para la cual se desean listar los asientos.
     * @param emailUsuario Correo electrónico del usuario que solicita la lista de asientos, utilizado para identificar
     * si algún asiento está retenido por ese usuario.
     * @return Una lista de DTOs que representan los asientos de la función, incluyendo su estado
     * y si están retenidos por el usuario solicitante.
     * @throws IllegalArgumentException Si la función no existe o si el usuario no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FuncionAsientoResponseDTO> listarAsientos(Long funcionId, String emailUsuario) {

        // Validar existe función
        if (!funcionRepository.existsById(funcionId)) {
            throw new IllegalArgumentException("Función no existe");
        }


        UsuarioEntity usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        Long userId = usuario.getId();

        List<FuncionAsientoEntity> lista = funcionAsientoRepository.findAllByFuncionIdWithAsiento(funcionId);

        return lista.stream().map(fa -> new FuncionAsientoResponseDTO(
                fa.getId(),
                fa.getAsiento().getId(),
                fa.getAsiento().getFila(),
                fa.getAsiento().getNumero(),
                fa.getAsiento().getTipo(),
                fa.getEstado().name(),
                (fa.getRetenidoPor() != null && fa.getRetenidoPor().getId().equals(userId)),
                fa.getRetencionExpira()
        )).toList();
    }

    /** Permite a un usuario retener (hold) uno o más asientos para una función específica en el sistema CINEMAX.
     * Verifica que la función exista, que el usuario exista y que los asientos solicitados sean válidos
     * @param funcionId ID de la función para la cual se desean retener los asientos.
     * @param emailUsuario  Correo electrónico del usuario que desea retener los asientos, utilizado para identificar al usuario
     * @param request DTO que contiene la lista de IDs de los asientos que se desean retener.
     * @return  DTO con la información de los asientos retenidos, incluyendo el ID de la función, los IDs de los
     * asientos y la fecha y hora de expiración del hold.
     */
    @Override
    public HoldAsientosResponseDTO hold(Long funcionId, String emailUsuario, HoldAsientosRequestDTO request) {

        if (!funcionRepository.existsById(funcionId)) {
            throw new IllegalArgumentException("Función no existe");
        }

        UsuarioEntity usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        LocalDateTime now = LocalDateTime.now();

        // 1) Liberar asientos que hayan expirado (si alguien más los tenía en hold pero ya pasó el tiempo)
        funcionAsientoRepository.liberarExpirados(funcionId, now);

        // 2) Bloquear los asientos solicitados para validarlos (SELECT FOR UPDATE)
        List<Long> ids = request.funcionAsientoIds();
        List<FuncionAsientoEntity> filas = funcionAsientoRepository.findForUpdate(funcionId, ids);

        if (filas.size() != ids.size()) {
            throw new IllegalArgumentException("Algunos asientos no pertenecen a esta función o no existen");
        }
        // 3) Validar que todos los asientos estén disponibles o ya estén en hold por mí (en cuyo caso renovamos el hold)
        for (FuncionAsientoEntity fa : filas) {
            // Si el asiento está bloqueado pero el hold expiró, lo liberamos automáticamente para que pueda ser reservado por otros
            if (fa.getEstado() == EstadoFuncionAsiento.BLOQUEADO && fa.getRetencionExpira() != null
                    && fa.getRetencionExpira().isBefore(now)) {
                fa.setEstado(EstadoFuncionAsiento.DISPONIBLE);
                fa.setRetenidoPor(null);
                fa.setRetencionExpira(null);
            }

            boolean disponible = fa.getEstado() == EstadoFuncionAsiento.DISPONIBLE;
            boolean mio = fa.getEstado() == EstadoFuncionAsiento.BLOQUEADO
                    && fa.getRetenidoPor() != null
                    && fa.getRetenidoPor().getId().equals(usuario.getId())
                    && fa.getRetencionExpira() != null
                    && fa.getRetencionExpira().isAfter(now);

            if (!disponible && !mio) {
                throw new IllegalStateException("Al menos un asiento no está disponible para reservar (hold).");
            }
        }
        // 4) Si llegamos aquí, todos los asientos son válidos para poner en hold. Actualizamos su estado, el usuario
        // que los retiene y la fecha de expiración del hold.
        LocalDateTime expiraEn = now.plusMinutes(holdMinutes);
        for (FuncionAsientoEntity fa : filas) {
            fa.setEstado(EstadoFuncionAsiento.BLOQUEADO);
            fa.setRetenidoPor(usuario);
            fa.setRetencionExpira(expiraEn);
        }

        funcionAsientoRepository.saveAll(filas);

        return new HoldAsientosResponseDTO(funcionId, ids, expiraEn);
    }
    /* Permite a un usuario liberar uno o más asientos que tenía en hold para una función específica en el sistema CINEMAX.
 Verifica que la función exista, que el usuario exista y que los asientos solicitados sean válidos y estén actualmente en hold por ese usuario.
 * @param funcionId ID de la función para la cual se desean liberar los asientos.
 * @param emailUsuario Correo electrónico del usuario que desea liberar los asientos, utilizado para identificar al usuario
 * @param request DTO que contiene la lista de IDs de los asientos que se desean liberar.
 * @return DTO con un mensaje indicando cuántos asientos fueron liberados exitosamente.
 * @throws IllegalArgumentException Si la función no existe, si el usuario no existe, si algunos asientos no pertenecen
  a la función o si algunos asientos no están actualmente en hold por ese usuario.
 */
    @Override
    public MessageResponseDTO release(Long funcionId, String emailUsuario, ReleaseAsientosRequestDTO request) {

        if (!funcionRepository.existsById(funcionId)) {
            throw new IllegalArgumentException("Función no existe");
        }

        UsuarioEntity usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        int liberados = funcionAsientoRepository.liberarPorUsuario(funcionId, request.funcionAsientoIds(), usuario.getId());
        return new MessageResponseDTO("Asientos liberados: " + liberados);
    }
}