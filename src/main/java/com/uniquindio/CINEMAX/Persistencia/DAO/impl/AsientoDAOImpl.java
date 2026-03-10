package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.AsientoDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.AsientoEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.SalaEntity;
import com.uniquindio.CINEMAX.Persistencia.Entity.TipoAsiento;
import com.uniquindio.CINEMAX.Persistencia.Repository.AsientoRepository;
import com.uniquindio.CINEMAX.Persistencia.Repository.SalaRepository;
import com.uniquindio.CINEMAX.negocio.DTO.AsientoPosDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
/* Implementación de la interfaz AsientoDAO para gestionar las operaciones relacionadas
 con los asientos en el sistema CINEMAX. */
@Repository
@RequiredArgsConstructor
@Transactional
public class AsientoDAOImpl implements AsientoDAO {

    private final AsientoRepository asientoRepository;
    private final SalaRepository salaRepository;

    /**
     * Genera los asientos para una sala específica según la configuración proporcionada en el DTO.
     * @param salaId ID de la sala para la cual se generarán los asientos.
     * @param dto DTO que contiene la configuración para generar los asientos, incluyendo filas, número de asientos por fila,
     * @return DTO con el resultado de la operación, incluyendo el número de asientos creados, actualizados,
     * desactivados y el total de asientos activos en la sala.
     */
    @Override
    public AsientosGenerarResponseDTO generar(Long salaId, AsientosGenerarDTO dto) {

        SalaEntity sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new IllegalArgumentException("Sala no existe"));

        if (!Boolean.TRUE.equals(sala.getActiva())) {
            throw new IllegalArgumentException("La sala está inactiva");
        }

        // Normalizar filas: eliminar espacios, convertir a mayúsculas, eliminar duplicados manteniendo orden
        Set<String> filas = dto.getFilas().stream()
                .map(f -> f.trim().toUpperCase())
                .filter(f -> !f.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (filas.isEmpty()) throw new IllegalArgumentException("Debes enviar al menos una fila");
        if (dto.getAsientosPorFila() < 1) throw new IllegalArgumentException("asientosPorFila debe ser >= 1");

        Set<String> vipFilas = dto.getVipFilas() == null ? Set.of() :
                dto.getVipFilas().stream().map(f -> f.trim().toUpperCase()).collect(Collectors.toSet());

        Set<String> discapacidadKeys = new HashSet<>();
        if (dto.getDiscapacidad() != null) {
            for (AsientoPosDTO p : dto.getDiscapacidad()) {
                discapacidadKeys.add(key(p.fila().trim().toUpperCase(), p.numero()));
            }
        }
        // Obtener los asientos existentes de la sala y mapearlos por fila-número para fácil acceso
        List<AsientoEntity> existentes = asientoRepository.findBySalaId(salaId);
        Map<String, AsientoEntity> map = new HashMap<>();
        for (AsientoEntity a : existentes) {
            map.put(key(a.getFila().toUpperCase(), a.getNumero()), a);
        }

        Set<String> deseados = new HashSet<>();
        int creados = 0, actualizados = 0, desactivados = 0;

        List<AsientoEntity> paraGuardar = new ArrayList<>();

        for (String fila : filas) {
            for (int numero = 1; numero <= dto.getAsientosPorFila(); numero++) {
                String k = key(fila, numero);
                deseados.add(k);

                TipoAsiento tipo = dto.getTipoDefault();
                if (vipFilas.contains(fila)) tipo = TipoAsiento.VIP;
                if (discapacidadKeys.contains(k)) tipo = TipoAsiento.DISCAPACIDAD;

                AsientoEntity existente = map.get(k);
                if (existente == null) {
                    // Crear
                    AsientoEntity nuevo = AsientoEntity.builder()
                            .sala(sala)
                            .fila(fila)
                            .numero(numero)
                            .tipo(tipo)
                            .activo(true)
                            .build();
                    paraGuardar.add(nuevo);
                    creados++;
                } else {
                    // Reactivar/actualizar
                    boolean cambio = false;
                    if (!Boolean.TRUE.equals(existente.getActivo())) { existente.setActivo(true); cambio = true; }
                    if (existente.getTipo() != tipo) { existente.setTipo(tipo); cambio = true; }
                    if (cambio) {
                        paraGuardar.add(existente);
                        actualizados++;
                    }
                }
            }
        }
        // Desactivar los asientos que ya no se desean, si se indicó en el DTO
        if (dto.isDesactivarFuera()) {
            for (AsientoEntity a : existentes) {
                String k = key(a.getFila().toUpperCase(), a.getNumero());
                if (!deseados.contains(k) && Boolean.TRUE.equals(a.getActivo())) {
                    a.setActivo(false);
                    paraGuardar.add(a);
                    desactivados++;
                }
            }
        }

        if (!paraGuardar.isEmpty()) {
            asientoRepository.saveAll(paraGuardar);
        }

        long totalActivos = asientoRepository.countBySalaIdAndActivoTrue(salaId);

        return new AsientosGenerarResponseDTO(salaId, creados, actualizados, desactivados, totalActivos);
    }

    /**
     * Lista los asientos de una sala específica, devolviendo un DTO con la información relevante de cada asiento.
     * @param salaId ID de la sala para la cual se listarán los asientos.
     * @return Lista de DTOs con la información de los asientos de la sala, incluyendo ID, fila, número, tipo y estado activo.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AsientoResponseDTO> listarPorSala(Long salaId) {
        return asientoRepository.findBySalaId(salaId).stream()
                .map(a -> new AsientoResponseDTO(a.getId(), a.getFila(), a.getNumero(), a.getTipo(), a.getActivo()))
                .toList();
    }
    // Método auxiliar para generar una clave única basada en la fila y el número del asiento,
    // utilizado para mapear y comparar asientos.
    private String key(String fila, int numero) {
        return fila + "-" + numero;
    }
}