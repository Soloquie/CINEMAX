package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.CineResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.CineUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.CineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cines")
@RequiredArgsConstructor
public class AdminCineController {

    private final CineService cineService;

    @PostMapping
    public CineResponseDTO crear(@Valid @RequestBody CineUpsertDTO dto) {
        return cineService.crear(dto);
    }

    @PutMapping("/{id}")
    public CineResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody CineUpsertDTO dto) {
        return cineService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        cineService.eliminar(id);
    }

    @GetMapping
    public List<CineResponseDTO> listar() {
        return cineService.listar();
    }
}