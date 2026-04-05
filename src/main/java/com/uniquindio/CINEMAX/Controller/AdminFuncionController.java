package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.FuncionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para la gestión de funciones en el sistema CINEMAX.
 * Este controlador proporciona endpoints para crear, actualizar, cancelar y listar funciones.
 */
@RestController
@RequestMapping("/api/admin/funciones")
@RequiredArgsConstructor
public class AdminFuncionController {

    private final FuncionService funcionService;
    /**
     * Endpoint para crear una nueva función.
     * @param dto Un objeto FuncionUpsertDTO que contiene la información necesaria para crear una función (película, sala, horario, etc.).
     * @return Un objeto FuncionResponseDTO que contiene la información de la función creada, incluyendo su ID generado.
     */
    @PostMapping
    public FuncionResponseDTO crear(@Valid @RequestBody FuncionUpsertDTO dto) {
        return funcionService.crear(dto);
    }
    /**
     * Endpoint para actualizar una función existente.
     * @param id El ID de la función que se desea actualizar.
     * @param dto Un objeto FuncionUpsertDTO que contiene la información actualizada de la función (película, sala, horario, etc.).
     * @return Un objeto FuncionResponseDTO que contiene la información de la función actualizada.
     */
    @PutMapping("/{id}")
    public FuncionResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody FuncionUpsertDTO dto) {
        return funcionService.actualizar(id, dto);
    }
    /**
     * Endpoint para cancelar una función existente.
     * @param id El ID de la función que se desea cancelar.
     * @return Un objeto FuncionResponseDTO que contiene la información de la función cancelada, incluyendo su estado actualizado a "cancelada".
     */
    @DeleteMapping("/{id}")
    public FuncionResponseDTO cancelar(@PathVariable Long id) {
        return funcionService.cancelar(id);
    }
    /**
     * Endpoint para listar todas las funciones existentes.
     * @return Una lista de objetos FuncionResponseDTO que contienen la información de todas las funciones en el sistema, incluyendo su estado (activa o cancelada).
     */
    @GetMapping
    public List<FuncionResponseDTO> listarTodas() {
        return funcionService.listarTodasAdmin();
    }
}