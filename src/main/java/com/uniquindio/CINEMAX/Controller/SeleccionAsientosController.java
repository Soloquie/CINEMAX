package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.SeleccionAsientosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
public class SeleccionAsientosController {

    private final SeleccionAsientosService service;

    @GetMapping("/{funcionId}/asientos")
    public List<FuncionAsientoResponseDTO> listar(@PathVariable Long funcionId, Authentication auth) {
        return service.listarAsientos(funcionId, auth.getName());
    }

    @PostMapping("/{funcionId}/asientos/hold")
    public HoldAsientosResponseDTO hold(@PathVariable Long funcionId,
                                        @Valid @RequestBody HoldAsientosRequestDTO request,
                                        Authentication auth) {
        return service.hold(funcionId, auth.getName(), request);
    }

    @PostMapping("/{funcionId}/asientos/release")
    public MessageResponseDTO release(@PathVariable Long funcionId,
                                      @Valid @RequestBody ReleaseAsientosRequestDTO request,
                                      Authentication auth) {
        return service.release(funcionId, auth.getName(), request);
    }
}