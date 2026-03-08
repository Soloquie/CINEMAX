package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.Entity.EstadoFuncion;
import com.uniquindio.CINEMAX.Persistencia.Repository.CineRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.FuncionRepository;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionPublicDTO;
import com.uniquindio.CINEMAX.negocio.Service.FuncionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuncionesServiceImpl implements FuncionesService {

    private final FuncionRepository funcionRepository;
    private final CineRepository cineRepository;

    @Override
    public List<FuncionPublicDTO> listar(LocalDate fecha, Long cineId, Long peliculaId) {
        LocalDate day = (fecha == null) ? LocalDate.now() : fecha;

        // Regla de negocio: si es HOY, no mostrar funciones que ya empezaron
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime desde = day.atStartOfDay();
        if (day.equals(now.toLocalDate())) {
            desde = now;
        }
        LocalDateTime hasta = day.plusDays(1).atStartOfDay();

        if (cineId != null) {
            var cine = cineRepository.findById(cineId).orElseThrow(() -> new IllegalArgumentException("Cine no existe."));
            if (!Boolean.TRUE.equals(cine.getActivo())) throw new IllegalArgumentException("Cine inactivo.");
        }

        var list = funcionRepository.buscarFunciones(
                EstadoFuncion.PROGRAMADA,
                desde,
                hasta,
                cineId,
                peliculaId
        );

        return list.stream().map(f -> new FuncionPublicDTO(
                f.getId(),
                f.getPelicula().getId(),
                f.getPelicula().getTitulo(),
                f.getPelicula().getPosterUrl(),
                f.getSala().getCine().getId(),
                f.getSala().getCine().getNombre(),
                f.getSala().getId(),
                f.getSala().getNombre(),
                f.getInicio(),
                f.getFin(),
                f.getIdioma(),
                Boolean.TRUE.equals(f.getSubtitulos()),
                f.getPrecioBase(),
                f.getEstado().name()
        )).toList();
    }

    @Override
    public List<FuncionPublicDTO> listarPorCine(Long cineId, LocalDate fecha) {
        if (cineId == null) throw new IllegalArgumentException("cineId es requerido.");
        return listar(fecha, cineId, null);
    }
}