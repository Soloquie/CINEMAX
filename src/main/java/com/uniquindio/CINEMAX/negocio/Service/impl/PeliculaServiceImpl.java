package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.PeliculaDAO;
import com.uniquindio.CINEMAX.Persistencia.Mapper.PeliculaMapper;
import com.uniquindio.CINEMAX.negocio.DTO.PeliculaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PeliculaUpsertFormDTO;
import com.uniquindio.CINEMAX.negocio.Exception.PeliculaNoEncontradaException;
import com.uniquindio.CINEMAX.negocio.Model.Pelicula;
import com.uniquindio.CINEMAX.negocio.Service.CloudinaryService;
import com.uniquindio.CINEMAX.negocio.Service.PeliculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
/**
 * Implementación de la interfaz PeliculaService para gestionar las películas en el sistema CINEMAX.
 * Esta clase utiliza un DAO para interactuar con la base de datos, un mapper para convertir entre entidades y DTOs,
 * y un servicio de Cloudinary para manejar la carga de carteles de películas.
 */
@Service
@RequiredArgsConstructor
public class PeliculaServiceImpl implements PeliculaService {
    /* Implementación del método para crear una nueva película. */
    private final PeliculaDAO peliculaDAO;
    private final PeliculaMapper peliculaMapper;
    private final CloudinaryService cloudinaryService;

    /**
     * Crea una nueva película en el sistema CINEMAX. Este método maneja la carga del cartel de la película a Cloudinary,
     * y luego guarda la película en la base de datos utilizando el DAO.
     * Finalmente, convierte la entidad guardada a un DTO de respuesta y lo devuelve.
     * @param form Formulario que contiene los datos necesarios para crear una nueva película, incluyendo el título,
     * sinopsis, duración, clasificación, fecha de estreno, estado de actividad y los IDs de los géneros asociados.
     * @return DTO de respuesta que representa la película creada, incluyendo su ID, título, sinopsis, duración,
     * clasificación, fecha de estreno, URL del cartel, estado de actividad y los géneros asociados.
     */
    @Override
    @Transactional
    public PeliculaResponseDTO crear(PeliculaUpsertFormDTO form) {
        String posterUrl = cloudinaryService.uploadPoster(form.getPoster());

        Pelicula toSave = new Pelicula(
                null,
                form.getTitulo(),
                form.getSinopsis(),
                form.getDuracionMin(),
                form.getClasificacion(),
                form.getFechaEstreno(),
                posterUrl,
                form.getActiva(),
                new HashSet<>()
        );

        Pelicula saved = peliculaDAO.guardar(toSave, form.getGeneroIds());
        return peliculaMapper.toDTO(saved);
    }
    /**
     * Actualiza una película existente en el sistema CINEMAX. Este método primero verifica si la película existe,
     * luego maneja la posible carga de un nuevo cartel a Cloudinary, y finalmente actualiza la película en la base de datos utilizando el DAO.
     * Finalmente, convierte la entidad actualizada a un DTO de respuesta y lo devuelve.
     * @param id ID de la película que se desea actualizar. Debe ser un ID válido de una película existente en el sistema.
     * @param form Formulario que contiene los datos necesarios para actualizar la película, incluyendo el título,
     * sinopsis, duración, clasificación, fecha de estreno, estado de actividad y los IDs de los géneros asociados.
     * @return DTO de respuesta que representa la película actualizada, incluyendo su ID, título, sinopsis, duración,
     * clasificación, fecha de estreno, URL del cartel, estado de actividad y los géneros asociados.
     * @throws PeliculaNoEncontradaException si la película con el ID proporcionado no existe en el sistema.
     */
    @Override
    @Transactional
    public PeliculaResponseDTO actualizar(Long id, PeliculaUpsertFormDTO form) {
        Pelicula existente = peliculaDAO.buscarPorId(id)
                .orElseThrow(PeliculaNoEncontradaException::new);

        String posterUrl = existente.posterUrl();
        String nuevoPoster = cloudinaryService.uploadPoster(form.getPoster());
        if (nuevoPoster != null) posterUrl = nuevoPoster;

        Pelicula toSave = new Pelicula(
                id,
                form.getTitulo(),
                form.getSinopsis(),
                form.getDuracionMin(),
                form.getClasificacion(),
                form.getFechaEstreno(),
                posterUrl,
                form.getActiva(),
                existente.generos()
        );

        Pelicula saved = peliculaDAO.guardar(toSave, form.getGeneroIds());
        return peliculaMapper.toDTO(saved);
    }
    /**
     * Elimina una película del sistema CINEMAX por su ID. Este método utiliza el DAO para eliminar la película de la base de datos.
     * @param id ID de la película que se desea eliminar. Debe ser un ID válido de una película existente en el sistema.
     * @throws PeliculaNoEncontradaException si la película con el ID proporcionado no existe en el sistema.
     */
    @Override
    public void eliminar(Long id) {
        peliculaDAO.buscarPorId(id)
                .orElseThrow(PeliculaNoEncontradaException::new);
        peliculaDAO.eliminar(id);
    }
    /**
     * Lista todas las películas disponibles en el sistema CINEMAX, incluyendo tanto las activas como las inactivas. Este método utiliza el DAO para obtener la lista de películas,
     * luego convierte cada entidad a un DTO de respuesta utilizando el mapper, y finalmente devuelve la lista de DTOs.
     * @return Lista de DTOs de respuesta que representan todas las películas en el sistema, incluyendo su ID, título, sinopsis, duración, clasificación, fecha de estreno,
     * URL del cartel, estado de actividad y los géneros asociados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PeliculaResponseDTO> listarTodasAdmin() {
        return peliculaDAO.listarTodas().stream().map(peliculaMapper::toDTO).toList();
    }
    /**
     * Lista solo las películas activas disponibles en el sistema CINEMAX. Este método utiliza el DAO para obtener la lista de películas activas,
     * luego convierte cada entidad a un DTO de respuesta utilizando el mapper, y finalmente devuelve la lista de DTOs.
     * @return Lista de DTOs de respuesta que representan solo las películas activas en el sistema, incluyendo su ID, título, sinopsis, duración, clasificación, fecha de estreno,
     * URL del cartel, estado de actividad y los géneros asociados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PeliculaResponseDTO> listarActivas() {
        return peliculaDAO.listarActivas().stream().map(peliculaMapper::toDTO).toList();
    }
}