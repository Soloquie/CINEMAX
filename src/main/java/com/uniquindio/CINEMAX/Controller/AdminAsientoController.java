package com.uniquindio.CINEMAX.Controller;
import com.uniquindio.CINEMAX.negocio.DTO.AsientoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.AsientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para la gestión de asientos en el sistema CINEMAX.
 * Este controlador proporciona endpoints para generar asientos en una
 * sala específica y para listar los asientos
 */
@RestController
@RequiredArgsConstructor
public class AdminAsientoController {
    private final AsientoService asientoService;
    /**
     * Endpoint para generar asientos en una sala específica.
     * @param salaId El ID de la sala para la cual se generarán los asientos.
     * @param dto Un objeto AsientosGenerarDTO que contiene la configuración para generar los asientos (número de filas, número de columnas, etc.).
     * @return Un objeto AsientosGenerarResponseDTO que contiene información sobre los asientos generados, como el número total de asientos creados.
     */
    @PostMapping("/api/admin/salas/{salaId}/asientos/generar")
    public AsientosGenerarResponseDTO generar(@PathVariable Long salaId, @Valid @RequestBody AsientosGenerarDTO dto) {
        return asientoService.generar(salaId, dto);
    }
    /**
     * Endpoint para listar los asientos de una sala específica.
     * @param salaId
     * @return Lista de asientos en formato AsientoResponseDTO para la sala especificada por salaId.
     */
    @GetMapping("/api/admin/salas/{salaId}/asientos")
    public List<AsientoResponseDTO> listar(@PathVariable Long salaId) {
        return asientoService.listarPorSala(salaId);
    }
}