package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.AsientoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.AsientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminAsientoController {

    private final AsientoService asientoService;

    @PostMapping("/api/admin/salas/{salaId}/asientos/generar")
    public AsientosGenerarResponseDTO generar(@PathVariable Long salaId, @Valid @RequestBody AsientosGenerarDTO dto) {
        return asientoService.generar(salaId, dto);
    }

    @GetMapping("/api/admin/salas/{salaId}/asientos")
    public List<AsientoResponseDTO> listar(@PathVariable Long salaId) {
        return asientoService.listarPorSala(salaId);
    }
}