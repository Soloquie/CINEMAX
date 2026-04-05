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
/* Implementación de la interfaz CineDAO para gestionar las operaciones relacionadas
con los cines en el sistema CINEMAX. */
@Repository
@RequiredArgsConstructor
@Transactional
public class CineDAOImpl implements CineDAO {

    private final CineRepository cineRepository;

    /**
     * Crea un nuevo cine en el sistema CINEMAX. Verifica que no exista un cine con el mismo nombre y ciudad antes de guardarlo.
     * @param dto DTO que contiene la información necesaria para crear un nuevo cine, incluyendo nombre, ciudad, dirección y estado activo.
     * @return  DTO con la información del cine creado, incluyendo su ID, nombre, ciudad, dirección y estado activo.
     */
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
    /**
     * Actualiza la información de un cine existente en el sistema CINEMAX. Verifica que el cine exista antes de actualizarlo.
     * @param id ID del cine que se desea actualizar.
     * @param dto DTO que contiene la información actualizada del cine, incluyendo nombre, ciudad, dirección y estado activo.
     * @return DTO con la información del cine actualizado, incluyendo su ID, nombre, ciudad, dirección y estado activo.
     */
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
    /**
     * Elimina un cine del sistema CINEMAX de forma lógica (soft delete) estableciendo su estado activo a false.
     * Verifica que el cine exista antes de eliminarlo.
     * @param id ID del cine que se desea eliminar.
     */
    @Override
    public void eliminar(Long id) {
        CineEntity e = cineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cine no existe"));
        e.setActivo(false);
        cineRepository.save(e);
    }
    /**
     * Lista todos los cines disponibles en el sistema CINEMAX, devolviendo un DTO con la información relevante de cada cine.
     * @return Lista de DTOs con la información de los cines, incluyendo ID, nombre, ciudad, dirección y estado activo.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CineResponseDTO> listar() {
        return cineRepository.findAll().stream().map(this::toDTO).toList();
    }
    /**
     * Convierte una entidad de cine a un DTO de respuesta, extrayendo la información relevante del cine.
     * @param e Entidad de cine que se desea convertir a DTO.
     * @return DTO con la información del cine, incluyendo su ID, nombre, ciudad, dirección y estado activo.
     */
    private CineResponseDTO toDTO(CineEntity e) {
        return new CineResponseDTO(e.getId(), e.getNombre(), e.getCiudad(), e.getDireccion(), e.getActivo());
    }
}