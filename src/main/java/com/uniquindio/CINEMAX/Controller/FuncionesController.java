package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.FuncionPublicDTO;
import com.uniquindio.CINEMAX.negocio.Service.FuncionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
/**
 * Controlador REST para la gestión de funciones en el sistema CINEMAX.
 * Este controlador proporciona endpoints para listar funciones disponibles con filtros específicos.
 */
@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
public class FuncionesController {

    private final FuncionesService funcionesService;
    /**
     * Endpoint para listar funciones disponibles con filtros opcionales.
     * @param fecha Un filtro opcional para listar funciones en una fecha específica (formato ISO: yyyy-MM-dd).
     * @param cineId Un filtro opcional para listar funciones de un cine específico.
     * @param peliculaId Un filtro opcional para listar funciones de una película específica.
     * @return Una lista de objetos FuncionPublicDTO que contienen la información de las funciones que cumplen con los filtros aplicados.
     */
    @GetMapping
    public List<FuncionPublicDTO> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Long cineId,
            @RequestParam(required = false) Long peliculaId
    ) {
        return funcionesService.listar(fecha, cineId, peliculaId);
    }
    /**
     * Endpoint para listar funciones de un cine específico con un filtro opcional por fecha.
     * @param cineId El ID del cine para el cual se desean listar las funciones.
     * @param fecha Un filtro opcional para listar funciones en una fecha específica (formato ISO: yyyy-MM-dd).
     * @return Una lista de objetos FuncionPublicDTO que contienen la información de las funciones disponibles en el cine especificado, filtradas por fecha si se proporciona el parámetro.
     */
    @GetMapping("/cine/{cineId}")
    public List<FuncionPublicDTO> listarPorCine(
            @PathVariable Long cineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return funcionesService.listarPorCine(cineId, fecha);
    }
}