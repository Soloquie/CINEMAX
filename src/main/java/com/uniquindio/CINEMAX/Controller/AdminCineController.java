package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.CineResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.CineUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.CineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para la gestión de cines en el sistema CINEMAX.
 * Este controlador proporciona endpoints para crear, actualizar, eliminar y listar cines.
 */
@RestController
@RequestMapping("/api/admin/cines")
@RequiredArgsConstructor
public class AdminCineController {

    private final CineService cineService;
    /**
     * Endpoint para crear un nuevo cine.
     * @param dto Un objeto CineUpsertDTO que contiene la información necesaria para crear un cine (nombre, dirección, etc.).
     * @return Un objeto CineResponseDTO que contiene la información del cine creado, incluyendo su ID generado.
     */
    @PostMapping
    public CineResponseDTO crear(@Valid @RequestBody CineUpsertDTO dto) {
        return cineService.crear(dto);
    }
    /**
     * Endpoint para actualizar un cine existente.
     * @param id El ID del cine que se desea actualizar.
     * @param dto Un objeto CineUpsertDTO que contiene la información actualizada del cine (nombre, dirección, etc.).
     * @return Un objeto CineResponseDTO que contiene la información del cine actualizado.
     */
    @PutMapping("/{id}")
    public CineResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody CineUpsertDTO dto) {
        return cineService.actualizar(id, dto);
    }
    /**
     * Endpoint para eliminar un cine existente.
     * @param id El ID del cine que se desea eliminar.
     * No retorna ningún contenido, solo indica que la operación fue exitosa mediante el código de estado HTTP.
     */
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        cineService.eliminar(id);
    }
    /**
     * Endpoint para listar todos los cines existentes.
     * @return Una lista de objetos CineResponseDTO que contienen la información de todos los cines en el sistema.
     */
    @GetMapping
    public List<CineResponseDTO> listar() {
        return cineService.listar();
    }
}