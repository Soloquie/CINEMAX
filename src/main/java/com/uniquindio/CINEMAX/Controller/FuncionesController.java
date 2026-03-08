package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.FuncionPublicDTO;
import com.uniquindio.CINEMAX.negocio.Service.FuncionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
public class FuncionesController {

    private final FuncionesService funcionesService;

    // Lista general con filtros exactos (fecha/cine/pelicula)
    @GetMapping
    public List<FuncionPublicDTO> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Long cineId,
            @RequestParam(required = false) Long peliculaId
    ) {
        return funcionesService.listar(fecha, cineId, peliculaId);
    }

    @GetMapping("/cine/{cineId}")
    public List<FuncionPublicDTO> listarPorCine(
            @PathVariable Long cineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return funcionesService.listarPorCine(cineId, fecha);
    }
}