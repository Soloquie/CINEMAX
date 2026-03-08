package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    // --- CINES ---
    @GetMapping("/cines")
    public List<CinePublicDTO> listarCines(@RequestParam(required = false) String ciudad) {
        return catalogoService.listarCines(ciudad);
    }

    @GetMapping("/cines/{cineId}/salas")
    public List<SalaPublicDTO> listarSalas(@PathVariable Long cineId) {
        return catalogoService.listarSalasPorCine(cineId);
    }

    // --- PELÍCULAS ---
    @GetMapping("/peliculas")
    public Page<PeliculaCardDTO> listarPeliculas(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long generoId,
            @RequestParam(required = false) String desde, // yyyy-MM-dd
            @RequestParam(required = false) String hasta, // yyyy-MM-dd
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return catalogoService.listarPeliculas(q, generoId, desde, hasta, page, size);
    }

    @GetMapping("/peliculas/{peliculaId}")
    public PeliculaDetailDTO detalle(@PathVariable Long peliculaId) {
        return catalogoService.detallePelicula(peliculaId);
    }

    @GetMapping("/peliculas/proximas")
    public List<PeliculaCardDTO> proximas(@RequestParam(defaultValue = "30") int dias) {
        return catalogoService.proximas(dias);
    }

    @GetMapping("/peliculas/en-cartelera")
    public List<PeliculaCardDTO> enCartelera() {
        return catalogoService.enCartelera();
    }

    @GetMapping("/generos")
    public List<GeneroDTO> listarGeneros() {
        return catalogoService.listarGeneros();
    }
}