package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.SalaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.SalaUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.SalaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminSalaController {

    private final SalaService salaService;

    @PostMapping("/api/admin/cines/{cineId}/salas")
    public SalaResponseDTO crear(@PathVariable Long cineId, @Valid @RequestBody SalaUpsertDTO dto) {
        return salaService.crear(cineId, dto);
    }

    @GetMapping("/api/admin/cines/{cineId}/salas")
    public List<SalaResponseDTO> listarPorCine(@PathVariable Long cineId) {
        return salaService.listarPorCine(cineId);
    }

    @PutMapping("/api/admin/salas/{salaId}")
    public SalaResponseDTO actualizar(@PathVariable Long salaId, @Valid @RequestBody SalaUpsertDTO dto) {
        return salaService.actualizar(salaId, dto);
    }

    @DeleteMapping("/api/admin/salas/{salaId}")
    public void eliminar(@PathVariable Long salaId) {
        salaService.eliminar(salaId);
    }
}