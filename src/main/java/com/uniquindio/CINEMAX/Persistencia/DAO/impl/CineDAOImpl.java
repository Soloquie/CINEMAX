package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.CineDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.CineEntity;
import com.uniquindio.CINEMAX.Persistencia.Repository.CineRepository;
import com.uniquindio.CINEMAX.negocio.DTO.CineResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.CineUpsertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class CineDAOImpl implements CineDAO {

    private final CineRepository cineRepository;

    @Override
    public CineResponseDTO crear(CineUpsertDTO dto) {
        String nombre = dto.nombre().trim();
        String ciudad = dto.ciudad().trim();

        if (cineRepository.existsByNombreAndCiudad(nombre, ciudad)) {
            throw new IllegalArgumentException("Ya existe un cine con ese nombre en esa ciudad");
        }

        CineEntity e = CineEntity.builder()
                .nombre(nombre)
                .ciudad(ciudad)
                .direccion(dto.direccion().trim())
                .activo(dto.activo() != null ? dto.activo() : true)
                .build();

        return toDTO(cineRepository.save(e));
    }

    @Override
    public CineResponseDTO actualizar(Long id, CineUpsertDTO dto) {
        CineEntity e = cineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cine no existe"));

        e.setNombre(dto.nombre().trim());
        e.setCiudad(dto.ciudad().trim());
        e.setDireccion(dto.direccion().trim());
        if (dto.activo() != null) e.setActivo(dto.activo());

        return toDTO(cineRepository.save(e));
    }

    @Override
    public void eliminar(Long id) {
        CineEntity e = cineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cine no existe"));
        e.setActivo(false);
        cineRepository.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CineResponseDTO> listar() {
        return cineRepository.findAll().stream().map(this::toDTO).toList();
    }

    private CineResponseDTO toDTO(CineEntity e) {
        return new CineResponseDTO(e.getId(), e.getNombre(), e.getCiudad(), e.getDireccion(), e.getActivo());
    }
}