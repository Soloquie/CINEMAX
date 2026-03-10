package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.FuncionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para la gestión de funciones en el sistema CINEMAX.
 * Este controlador proporciona endpoints para listar funciones programadas de una película específica.
 */
@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
public class FuncionPublicController {

    private final FuncionService funcionService;
    /**
     * Endpoint para listar funciones programadas de una película específica.
     * @param peliculaId El ID de la película para la cual se desean listar las funciones programadas.
     * @return Una lista de objetos FuncionResponseDTO que contienen la información de las funciones programadas para la película especificada.
     */
    @GetMapping("/pelicula/{peliculaId}")
    public List<FuncionResponseDTO> listarProgramadas(@PathVariable Long peliculaId) {
        return funcionService.listarProgramadasPorPelicula(peliculaId);
    }
}