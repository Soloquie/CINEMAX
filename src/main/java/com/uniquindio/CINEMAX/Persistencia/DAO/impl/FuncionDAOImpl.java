package com.uniquindio.CINEMAX.Persistencia.DAO.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.FuncionDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.*;
import com.uniquindio.CINEMAX.Persistencia.Repository.*;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionUpsertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
/* Implementación de la interfaz FuncionDAO para gestionar las operaciones relacionadas

 */
@Repository
@RequiredArgsConstructor
@Transactional
public class FuncionDAOImpl implements FuncionDAO {
    // Repositorios necesarios para realizar las operaciones relacionadas con las funciones, películas,
    // salas y asientos en el sistema CINEMAX.
    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;
    private final AsientoRepository asientoRepository;
    private final FuncionAsientoRepository funcionAsientoRepository;

    /**
     * Crea una nueva función en el sistema CINEMAX. Verifica que la película y la sala existan, y que la hora de fin
     * sea posterior a la hora de inicio.
     * @param dto DTO que contiene la información necesaria para crear una nueva función, incluyendo ID de película,
     * ID de sala, hora de inicio, hora de fin, idioma, subtítulos y precio base.
     * @return DTO con la información de la función creada, incluyendo su ID, título de película, nombre de sala,
     * nombre de cine, hora de inicio, hora de fin, idioma, subtítulos, precio base y estado de la función.
     */
    @Override
    public FuncionResponseDTO crear(FuncionUpsertDTO dto) {
        PeliculaEntity pelicula = peliculaRepository.findById(dto.peliculaId())
                .orElseThrow(() -> new IllegalArgumentException("Película no existe"));

        SalaEntity sala = salaRepository.findById(dto.salaId())
                .orElseThrow(() -> new IllegalArgumentException("Sala no existe"));

        LocalDateTime inicio = dto.inicio();
        LocalDateTime fin = dto.fin();

        if (fin == null) {
            fin = inicio.plusMinutes(pelicula.getDuracionMin());
        }

        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La hora fin debe ser mayor que inicio");
        }

        FuncionEntity entity = FuncionEntity.builder()
                .pelicula(pelicula)
                .sala(sala)
                .inicio(inicio)
                .fin(fin)
                .idioma(dto.idioma())
                .subtitulos(dto.subtitulos() != null && dto.subtitulos())
                .precioBase(dto.precioBase() != null ? dto.precioBase() : java.math.BigDecimal.ZERO)
                .estado(EstadoFuncion.PROGRAMADA)
                .build();

        try {
            FuncionEntity saved = funcionRepository.save(entity);
            inicializarAsientosFuncion(saved);
            return toDTO(saved);
        } catch (DataIntegrityViolationException ex) {

            throw new IllegalArgumentException("Ya existe una función en esa sala a esa hora de inicio");
        }
    }
    /**
     * Actualiza la información de una función existente en el sistema CINEMAX. Verifica que la función, película y sala existan,
     * y que la hora de fin sea posterior a la hora de inicio. Si se cambia la sala, se eliminan los registros de asientos
     * asociados a la función y se vuelven a crear para la nueva sala.
     * @param id ID de la función que se desea actualizar.
     * @param dto DTO que contiene la información actualizada de la función, incluyendo ID de película, ID de sala, hora de inicio,
     * hora de fin, idioma, subtítulos y precio base.
     * @return DTO con la información de la función actualizada, incluyendo su ID, título de película, nombre de sala, nombre de cine,
     * hora de inicio, hora de fin, idioma, subtítulos, precio base y estado de la función.
     */
    @Override
    public FuncionResponseDTO actualizar(Long id, FuncionUpsertDTO dto) {
        FuncionEntity entity = funcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Función no existe"));

        PeliculaEntity pelicula = peliculaRepository.findById(dto.peliculaId())
                .orElseThrow(() -> new IllegalArgumentException("Película no existe"));

        SalaEntity sala = salaRepository.findById(dto.salaId())
                .orElseThrow(() -> new IllegalArgumentException("Sala no existe"));

        LocalDateTime inicio = dto.inicio();
        LocalDateTime fin = dto.fin();
        if (fin == null) fin = inicio.plusMinutes(pelicula.getDuracionMin());

        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La hora fin debe ser mayor que inicio");
        }

        entity.setPelicula(pelicula);
        entity.setSala(sala);
        entity.setInicio(inicio);
        entity.setFin(fin);
        entity.setIdioma(dto.idioma());
        entity.setSubtitulos(dto.subtitulos() != null && dto.subtitulos());
        entity.setPrecioBase(dto.precioBase() != null ? dto.precioBase() : java.math.BigDecimal.ZERO);

        try {
            Long salaAnteriorId = entity.getSala().getId();
            boolean cambioSala = !salaAnteriorId.equals(dto.salaId());

            entity.setSala(sala);
            FuncionEntity saved = funcionRepository.save(entity);

            if (cambioSala) {
                funcionAsientoRepository.deleteByFuncionId(saved.getId());
                inicializarAsientosFuncion(saved);
            }
            return toDTO(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Ya existe una función en esa sala a esa hora de inicio");
        }
    }
    /**
     * Cancela una función existente en el sistema CINEMAX estableciendo su estado a CANCELADA.
     * Verifica que la función exista antes de cancelarla.
     * @param id ID de la función que se desea cancelar.
     * @return DTO con la información de la función cancelada, incluyendo su ID, título de película,
     * nombre de sala, nombre de cine, hora de inicio,
     * hora de fin, idioma, subtítulos, precio base y estado de la función (CANCELADA).
     */
    @Override
    public FuncionResponseDTO cancelar(Long id) {
        FuncionEntity entity = funcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Función no existe"));
        entity.setEstado(EstadoFuncion.CANCELADA);
        return toDTO(funcionRepository.save(entity));
    }
    /**
     * Lista todas las funciones disponibles en el sistema CINEMAX. Devuelve una lista de DTOs con la información de cada función,
     * incluyendo su ID, título de película, nombre de sala, nombre de cine, hora de inicio, hora de fin, idioma,
     * subtítulos, precio base y estado.
     * @return Lista de DTOs con la información de todas las funciones disponibles en el sistema CINEMAX.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FuncionResponseDTO> listarTodas() {
        return funcionRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuncionResponseDTO> listarProgramadasPorPelicula(Long peliculaId) {
        return funcionRepository
                .findByPeliculaIdAndEstadoAndInicioAfterOrderByInicioAsc(
                        peliculaId, EstadoFuncion.PROGRAMADA, LocalDateTime.now()
                )
                .stream()
                .map(this::toDTO)
                .toList();
    }
    /**
     * Convierte una entidad de función a un DTO de respuesta, extrayendo la información relevante de la función,
     * película, sala y cine asociados.
     * @param f Entidad de función que se desea convertir a DTO.
     * @return DTO con la información de la función, incluyendo su ID, título de película,
     * nombre de sala, nombre de cine, hora de inicio, hora de fin, idioma,
     * subtítulos, precio base y estado.
     */
    private FuncionResponseDTO toDTO(FuncionEntity f) {
        PeliculaEntity p = f.getPelicula();
        SalaEntity s = f.getSala();
        CineEntity c = s.getCine();

        return new FuncionResponseDTO(
                f.getId(),
                p.getId(),
                p.getTitulo(),
                p.getPosterUrl(),
                s.getId(),
                s.getNombre(),
                c.getId(),
                c.getNombre(),
                f.getInicio(),
                f.getFin(),
                f.getIdioma(),
                f.getSubtitulos(),
                f.getPrecioBase(),
                f.getEstado().name()
        );
    }
    // Método auxiliar para inicializar los registros de asientos asociados a una función. Verifica si ya existen
    // registros para la función antes de crearlos.
    private void inicializarAsientosFuncion(FuncionEntity funcion) {
        Long salaId = funcion.getSala().getId();

        if (funcionAsientoRepository.existsByFuncionId(funcion.getId())) {
            return;
        }

        var asientos = asientoRepository.findBySalaIdAndActivoTrue(salaId);
        if (asientos.isEmpty()) {
            throw new IllegalArgumentException("La sala no tiene asientos registrados. No se puede crear la función.");
        }

        var registros = asientos.stream()
                .map(a -> FuncionAsientoEntity.builder()
                        .funcion(funcion)
                        .asiento(a)
                        .estado(EstadoFuncionAsiento.DISPONIBLE)
                        .retenidoPor(null)
                        .retencionExpira(null)
                        .build()
                )
                .toList();

        funcionAsientoRepository.saveAll(registros);
    }

}