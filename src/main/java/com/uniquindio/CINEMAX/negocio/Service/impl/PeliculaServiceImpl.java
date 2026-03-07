package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.PeliculaDAO;
import com.uniquindio.CINEMAX.Persistencia.Mapper.PeliculaMapper;
import com.uniquindio.CINEMAX.negocio.DTO.PeliculaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.PeliculaUpsertFormDTO;
import com.uniquindio.CINEMAX.negocio.Model.Pelicula;
import com.uniquindio.CINEMAX.negocio.Service.CloudinaryService;
import com.uniquindio.CINEMAX.negocio.Service.PeliculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PeliculaServiceImpl implements PeliculaService {

    private final PeliculaDAO peliculaDAO;
    private final PeliculaMapper peliculaMapper;
    private final CloudinaryService cloudinaryService;

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

    @Override
    @Transactional
    public PeliculaResponseDTO actualizar(Long id, PeliculaUpsertFormDTO form) {
        Pelicula existente = peliculaDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Película no existe"));

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

    @Override
    public void eliminar(Long id) {
        peliculaDAO.eliminar(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeliculaResponseDTO> listarTodasAdmin() {
        return peliculaDAO.listarTodas().stream().map(peliculaMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeliculaResponseDTO> listarActivas() {
        return peliculaDAO.listarActivas().stream().map(peliculaMapper::toDTO).toList();
    }
}