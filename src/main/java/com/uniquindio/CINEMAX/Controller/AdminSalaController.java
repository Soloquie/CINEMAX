package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.SalaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.SalaUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.SalaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para la gestión de salas en el sistema CINEMAX.
 * Este controlador proporciona endpoints para crear, actualizar, eliminar y listar salas.
 */
@RestController
@RequiredArgsConstructor
public class AdminSalaController {

    private final SalaService salaService;
    /**
     * Endpoint para crear una nueva sala en un cine específico.
     * @param cineId El ID del cine al que pertenece la sala que se desea crear.
     * @param dto Un objeto SalaUpsertDTO que contiene la información necesaria para crear una sala (nombre, capacidad, etc.).
     * @return Un objeto SalaResponseDTO que contiene la información de la sala creada, incluyendo su ID generado.
     */
    @PostMapping("/api/admin/cines/{cineId}/salas")
    public SalaResponseDTO crear(@PathVariable Long cineId, @Valid @RequestBody SalaUpsertDTO dto) {
        return salaService.crear(cineId, dto);
    }
    /**
     * Endpoint para listar todas las salas de un cine específico.
     * @param cineId El ID del cine para el cual se desean listar las salas.
     * @return Una lista de objetos SalaResponseDTO que contienen la información de todas las salas pertenecientes al cine especificado por cineId.
     */
    @GetMapping("/api/admin/cines/{cineId}/salas")
    public List<SalaResponseDTO> listarPorCine(@PathVariable Long cineId) {
        return salaService.listarPorCine(cineId);
    }
    /**
     * Endpoint para actualizar una sala existente.
     * @param salaId El ID de la sala que se desea actualizar.
     * @param dto Un objeto SalaUpsertDTO que contiene la información actualizada de la sala (nombre, capacidad, etc.).
     * @return Un objeto SalaResponseDTO que contiene la información de la sala actualizada.
     */
    @PutMapping("/api/admin/salas/{salaId}")
    public SalaResponseDTO actualizar(@PathVariable Long salaId, @Valid @RequestBody SalaUpsertDTO dto) {
        return salaService.actualizar(salaId, dto);
    }
    /**
     * Endpoint para eliminar una sala existente.
     * @param salaId El ID de la sala que se desea eliminar.
     * No retorna ningún contenido, solo indica que la operación fue exitosa mediante el código de estado HTTP.
     */
    @DeleteMapping("/api/admin/salas/{salaId}")
    public void eliminar(@PathVariable Long salaId) {
        salaService.eliminar(salaId);
    }
}