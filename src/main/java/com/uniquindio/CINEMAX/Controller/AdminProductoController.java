package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.ActualizarStockDTO;
import com.uniquindio.CINEMAX.negocio.DTO.ProductoAdminResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.ProductoUpsertFormDTO;
import com.uniquindio.CINEMAX.negocio.Service.ConfiteriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductoAdminResponseDTO crear(@ModelAttribute @Valid ProductoUpsertFormDTO form) {
        return confiteriaService.crear(form);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductoAdminResponseDTO actualizar(@PathVariable Long id,
                                               @ModelAttribute @Valid ProductoUpsertFormDTO form) {
        return confiteriaService.actualizar(id, form);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        confiteriaService.eliminar(id);
    }

    @PatchMapping("/{id}/stock")
    public ProductoAdminResponseDTO actualizarStock(@PathVariable Long id,
                                                    @RequestBody @Valid ActualizarStockDTO dto) {
        return confiteriaService.actualizarStock(id, dto);
    }
}