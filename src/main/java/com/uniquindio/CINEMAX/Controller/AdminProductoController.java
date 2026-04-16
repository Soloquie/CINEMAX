package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.ConfiteriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/productos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductoController {

    private final ConfiteriaService confiteriaService;

    @GetMapping
    public List<ProductoAdminResponseDTO> listar() {
        return confiteriaService.listarAdmin();
    }

    @PostMapping
    public ProductoAdminResponseDTO crear(@Valid @RequestBody ProductoUpsertDTO dto) {
        return confiteriaService.crear(dto);
    }

    @PutMapping("/{id}")
    public ProductoAdminResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody ProductoUpsertDTO dto) {
        return confiteriaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        confiteriaService.eliminar(id);
    }

    @PatchMapping("/{id}/stock")
    public ProductoAdminResponseDTO actualizarStock(@PathVariable Long id, @Valid @RequestBody ActualizarStockDTO dto) {
        return confiteriaService.actualizarStock(id, dto);
    }
}