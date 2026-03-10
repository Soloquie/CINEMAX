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
/**
 * Controlador REST para la gestión de películas en el sistema CINEMAX.
 * Este controlador proporciona endpoints para crear, actualizar, eliminar y listar películas.
 * Solo los usuarios con el rol "ADMIN" pueden acceder a estos endpoints.
 */
@RestController
@RequestMapping("/api/admin/peliculas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPeliculaController {

    private final PeliculaService peliculaService;
    /**
     * Endpoint para crear una nueva película.
     * @param form Un objeto PeliculaUpsertFormDTO que contiene la información necesaria para crear una película (título, descripción, duración, etc.) y el archivo de imagen.
     * @return Un objeto PeliculaResponseDTO que contiene la información de la película creada, incluyendo su ID generado.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PeliculaResponseDTO crear(@ModelAttribute @Valid PeliculaUpsertFormDTO form) {
        return peliculaService.crear(form);
    }
    /**
     * Endpoint para actualizar una película existente.
     * @param id El ID de la película que se desea actualizar.
     * @param form Un objeto PeliculaUpsertFormDTO que contiene la información actualizada de la película (título, descripción, duración, etc.) y el archivo de imagen (opcional).
     * @return Un objeto PeliculaResponseDTO que contiene la información de la película actualizada.
     */
    @PutMapping(value="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PeliculaResponseDTO actualizar(@PathVariable Long id, @ModelAttribute PeliculaUpsertFormDTO form) {
        return peliculaService.actualizar(id, form);
    }
    /**
     * Endpoint para eliminar una película existente.
     * @param id El ID de la película que se desea eliminar.
     * No retorna ningún contenido, solo indica que la operación fue exitosa mediante el código de estado HTTP.
     */
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        peliculaService.eliminar(id);
    }
    /**
     * Endpoint para listar todas las películas existentes.
     * @return Una lista de objetos PeliculaResponseDTO que contienen la información de todas las películas en el sistema.
     */
    @GetMapping
    public List<PeliculaResponseDTO> listarTodasAdmin() {
        return peliculaService.listarTodasAdmin();
    }
}