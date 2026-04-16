package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.ProductoPublicDTO;
import com.uniquindio.CINEMAX.negocio.Service.ConfiteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/confiteria")
@RequiredArgsConstructor
public class ConfiteriaController {

    private final ConfiteriaService confiteriaService;

    @GetMapping("/productos")
    public List<ProductoPublicDTO> listar(@RequestParam(required = false) String categoria) {
        return confiteriaService.listarPublicos(categoria);
    }
}