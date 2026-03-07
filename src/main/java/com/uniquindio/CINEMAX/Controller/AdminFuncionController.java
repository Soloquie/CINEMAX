package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.FuncionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/funciones")
@RequiredArgsConstructor
public class AdminFuncionController {

    private final FuncionService funcionService;

    @PostMapping
    public FuncionResponseDTO crear(@Valid @RequestBody FuncionUpsertDTO dto) {
        return funcionService.crear(dto);
    }

    @PutMapping("/{id}")
    public FuncionResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody FuncionUpsertDTO dto) {
        return funcionService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public FuncionResponseDTO cancelar(@PathVariable Long id) {
        return funcionService.cancelar(id);
    }

    @GetMapping
    public List<FuncionResponseDTO> listarTodas() {
        return funcionService.listarTodasAdmin();
    }
}