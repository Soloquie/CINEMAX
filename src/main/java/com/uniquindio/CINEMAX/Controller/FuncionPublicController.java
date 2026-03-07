package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.FuncionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
public class FuncionPublicController {

    private final FuncionService funcionService;

    @GetMapping("/pelicula/{peliculaId}")
    public List<FuncionResponseDTO> listarProgramadas(@PathVariable Long peliculaId) {
        return funcionService.listarProgramadasPorPelicula(peliculaId);
    }
}