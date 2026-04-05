package com.uniquindio.CINEMAX.Controller;

import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.SeleccionAsientosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para la gestión de la selección de asientos en el sistema CINEMAX.
 * Este controlador proporciona endpoints para listar los asientos disponibles, realizar hold y release de asientos para una función específica.
 */
@RestController
@RequestMapping("/api/funciones")
@RequiredArgsConstructor
public class SeleccionAsientosController {

    private final SeleccionAsientosService service;
    /**
     * Endpoint para listar los asientos disponibles para una función específica.
     * @param funcionId El ID de la función para la cual se desean listar los asientos disponibles.
     * @param auth El objeto Authentication que contiene la información del usuario autenticado, incluyendo su nombre de usuario.
     * @return Una lista de objetos FuncionAsientoResponseDTO que contienen la información de los asientos disponibles para la función especificada, incluyendo su estado (disponible, ocupado, en hold, etc.).
     */
    @GetMapping("/{funcionId}/asientos")
    public List<FuncionAsientoResponseDTO> listar(@PathVariable Long funcionId, Authentication auth) {
        return service.listarAsientos(funcionId, auth.getName());
    }
    /**
     * Endpoint para realizar hold de asientos para una función específica.
     * @param funcionId El ID de la función para la cual se desea realizar hold de asientos.
     * @param request Un objeto HoldAsientosRequestDTO que contiene la información necesaria para realizar hold de asientos (lista de IDs de asientos, duración del hold, etc.).
     * @param auth El objeto Authentication que contiene la información del usuario autenticado, incluyendo su nombre de usuario.
     * @return Un objeto HoldAsientosResponseDTO que contiene la información del resultado del hold de asientos, incluyendo el estado actualizado de los asientos y cualquier mensaje relevante.
     */
    @PostMapping("/{funcionId}/asientos/hold")
    public HoldAsientosResponseDTO hold(@PathVariable Long funcionId,
                                        @Valid @RequestBody HoldAsientosRequestDTO request,
                                        Authentication auth) {
        return service.hold(funcionId, auth.getName(), request);
    }
    /**
     * Endpoint para realizar release de asientos para una función específica.
     * @param funcionId El ID de la función para la cual se desea realizar release de asientos.
     * @param request Un objeto ReleaseAsientosRequestDTO que contiene la información necesaria para realizar release de asientos (lista de IDs de asientos, etc.).
     * @param auth El objeto Authentication que contiene la información del usuario autenticado, incluyendo su nombre de usuario.
     * @return Un objeto MessageResponseDTO que contiene un mensaje indicando el resultado del release de asientos, incluyendo cualquier mensaje relevante sobre el estado actualizado de los asientos.
     */
    @PostMapping("/{funcionId}/asientos/release")
    public MessageResponseDTO release(@PathVariable Long funcionId,
                                      @Valid @RequestBody ReleaseAsientosRequestDTO request,
                                      Authentication auth) {
        return service.release(funcionId, auth.getName(), request);
    }
}