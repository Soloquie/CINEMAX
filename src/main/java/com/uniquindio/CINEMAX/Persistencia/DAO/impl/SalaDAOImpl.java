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
/* Implementación de la interfaz SalaDAO para gestionar las operaciones relacionadas con las salas en el sistema CINEMAX. */
@Repository
@RequiredArgsConstructor
@Transactional
public class SalaDAOImpl implements SalaDAO {

    private final SalaRepository salaRepository;
    private final CineRepository cineRepository;
    /* Crea una nueva sala en un cine específico del sistema CINEMAX. Verifica que el cine exista,
    esté activo y que no exista una sala con el mismo nombre en ese cine antes de guardarla.
     * @param cineId ID del cine al que se desea agregar la sala.
     * @param dto DTO que contiene la información necesaria para crear una nueva sala, incluyendo nombre, tipo y estado activo.
     * @return DTO con la información de la sala creada, incluyendo su ID, nombre, tipo,
     * estado activo y el ID y nombre del cine al que pertenece.
     */
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
    /* Actualiza la información de una sala existente en el sistema CINEMAX. Verifica que la sala exista antes de actualizarla.
     * @param salaId ID de la sala que se desea actualizar.
     * @param dto DTO que contiene la información actualizada de la sala, incluyendo nombre, tipo y estado activo.
     * @return DTO con la información de la sala actualizada, incluyendo su ID, nombre, tipo,
     * estado activo y el ID y nombre del cine al que pertenece.
     */
    @Override
    public SalaResponseDTO actualizar(Long salaId, SalaUpsertDTO dto) {
        SalaEntity s = salaRepository.findById(salaId)
                .orElseThrow(() -> new IllegalArgumentException("Sala no existe"));

        s.setNombre(dto.nombre().trim());
        s.setTipo(dto.tipo());
        if (dto.activa() != null) s.setActiva(dto.activa());

        return toDTO(salaRepository.save(s));
    }
    /* Elimina una sala por su ID en el sistema CINEMAX. En lugar de eliminar físicamente la sala de la base de datos, se marca como inactiva.
     * @param salaId ID de la sala a eliminar.
     * @throws IllegalArgumentException Si no se encuentra ninguna sala con ese ID.
     */
    @Override
    public void eliminar(Long salaId) {
        SalaEntity s = salaRepository.findById(salaId)
                .orElseThrow(() -> new IllegalArgumentException("Sala no existe"));
        s.setActiva(false);
        salaRepository.save(s);
    }
    /* Lista todas las salas de un cine específico en el sistema CINEMAX. Verifica que el cine exista antes de listar sus salas.
     * @param cineId ID del cine cuyas salas se desean listar.
     * @return Una lista de DTOs, cada uno con la información de una sala, incluyendo su ID, nombre, tipo,
     * estado activo y el ID y nombre del cine al que pertenece.
     * @throws IllegalArgumentException Si no se encuentra ningún cine con ese ID.
     */
    @Override
    @Transactional(readOnly = true)
    public List<SalaResponseDTO> listarPorCine(Long cineId) {
        return salaRepository.findByCineId(cineId).stream().map(this::toDTO).toList();
    }
    /* Convierte una entidad SalaEntity a un DTO SalaResponseDTO, extrayendo la información relevante de la sala y su cine asociado.
     * @param s La entidad SalaEntity que se desea convertir a DTO.
     * @return Un DTO SalaResponseDTO con la información de la sala, incluyendo su ID, nombre, tipo,
     * estado activo y el ID y nombre del cine al que pertenece.
     */
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