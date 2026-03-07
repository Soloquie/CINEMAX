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

@Repository
@RequiredArgsConstructor
@Transactional
public class SeleccionAsientosDAOImpl implements SeleccionAsientosDAO {

    private final FuncionRepository funcionRepository;
    private final FuncionAsientoRepository funcionAsientoRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.seat-hold.minutes:10}")
    private int holdMinutes;

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

    @Override
    public HoldAsientosResponseDTO hold(Long funcionId, String emailUsuario, HoldAsientosRequestDTO request) {

        if (!funcionRepository.existsById(funcionId)) {
            throw new IllegalArgumentException("Función no existe");
        }

        UsuarioEntity usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        LocalDateTime now = LocalDateTime.now();

        // 1) Liberar expirados (para que asientos vencidos vuelvan a DISPONIBLE)
        funcionAsientoRepository.liberarExpirados(funcionId, now);

        // 2) Traer y bloquear filas a actualizar
        List<Long> ids = request.funcionAsientoIds();
        List<FuncionAsientoEntity> filas = funcionAsientoRepository.findForUpdate(funcionId, ids);

        if (filas.size() != ids.size()) {
            throw new IllegalArgumentException("Algunos asientos no pertenecen a esta función o no existen");
        }

        // 3) Validar disponibilidad (ATÓMICO: si uno falla, no bloqueamos ninguno)
        for (FuncionAsientoEntity fa : filas) {

            // Si quedó BLOQUEADO pero ya expiró, lo consideramos DISPONIBLE (por si no liberó aún)
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

        // 4) Aplicar hold (o renovar si era mío)
        LocalDateTime expiraEn = now.plusMinutes(holdMinutes);
        for (FuncionAsientoEntity fa : filas) {
            fa.setEstado(EstadoFuncionAsiento.BLOQUEADO);
            fa.setRetenidoPor(usuario);
            fa.setRetencionExpira(expiraEn);
        }

        funcionAsientoRepository.saveAll(filas);

        return new HoldAsientosResponseDTO(funcionId, ids, expiraEn);
    }

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