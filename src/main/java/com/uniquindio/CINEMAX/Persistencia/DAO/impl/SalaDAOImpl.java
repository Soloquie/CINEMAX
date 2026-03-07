package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.SalaDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.CineEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.SalaEntity;
import com.uniquindio.CINEMAX.Persistencia.Repository.CineRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.SalaRepository;
import com.uniquindio.CINEMAX.negocio.DTO.SalaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.SalaUpsertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class SalaDAOImpl implements SalaDAO {

    private final SalaRepository salaRepository;
    private final CineRepository cineRepository;

    @Override
    public SalaResponseDTO crear(Long cineId, SalaUpsertDTO dto) {
        CineEntity cine = cineRepository.findById(cineId)
                .orElseThrow(() -> new IllegalArgumentException("Cine no existe"));

        if (!Boolean.TRUE.equals(cine.getActivo())) {
            throw new IllegalArgumentException("El cine está inactivo");
        }

        if (salaRepository.existsByCineIdAndNombre(cineId, dto.nombre().trim())) {
            throw new IllegalArgumentException("Ya existe una sala con ese nombre en este cine");
        }

        SalaEntity s = SalaEntity.builder()
                .cine(cine)
                .nombre(dto.nombre().trim())
                .tipo(dto.tipo())
                .activa(dto.activa() != null ? dto.activa() : true)
                .build();

        return toDTO(salaRepository.save(s));
    }

    @Override
    public SalaResponseDTO actualizar(Long salaId, SalaUpsertDTO dto) {
        SalaEntity s = salaRepository.findById(salaId)
                .orElseThrow(() -> new IllegalArgumentException("Sala no existe"));

        s.setNombre(dto.nombre().trim());
        s.setTipo(dto.tipo());
        if (dto.activa() != null) s.setActiva(dto.activa());

        return toDTO(salaRepository.save(s));
    }

    @Override
    public void eliminar(Long salaId) {
        SalaEntity s = salaRepository.findById(salaId)
                .orElseThrow(() -> new IllegalArgumentException("Sala no existe"));
        s.setActiva(false);
        salaRepository.save(s);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaResponseDTO> listarPorCine(Long cineId) {
        return salaRepository.findByCineId(cineId).stream().map(this::toDTO).toList();
    }

    private SalaResponseDTO toDTO(SalaEntity s) {
        return new SalaResponseDTO(
                s.getId(),
                s.getCine().getId(),
                s.getCine().getNombre(),
                s.getNombre(),
                s.getTipo(),
                s.getActiva()
        );
    }
}