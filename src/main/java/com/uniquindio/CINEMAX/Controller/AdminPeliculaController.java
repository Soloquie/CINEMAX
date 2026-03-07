package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.PeliculaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PeliculaUpsertFormDTO;
import com.uniquindio.CINEMAX.negocio.Service.PeliculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/peliculas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPeliculaController {

    private final PeliculaService peliculaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PeliculaResponseDTO crear(@ModelAttribute @Valid PeliculaUpsertFormDTO form) {
        return peliculaService.crear(form);
    }

    @PutMapping(value="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PeliculaResponseDTO actualizar(@PathVariable Long id, @ModelAttribute PeliculaUpsertFormDTO form) {
        return peliculaService.actualizar(id, form);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        peliculaService.eliminar(id);
    }

    @GetMapping
    public List<PeliculaResponseDTO> listarTodasAdmin() {
        return peliculaService.listarTodasAdmin();
    }
}