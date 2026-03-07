package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.SalaDAO;
import com.uniquindio.CINEMAX.negocio.DTO.SalaResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.SalaUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaServiceImpl implements SalaService {

    private final SalaDAO salaDAO;

    @Override public SalaResponseDTO crear(Long cineId, SalaUpsertDTO dto) { return salaDAO.crear(cineId, dto); }
    @Override public SalaResponseDTO actualizar(Long salaId, SalaUpsertDTO dto) { return salaDAO.actualizar(salaId, dto); }
    @Override public void eliminar(Long salaId) { salaDAO.eliminar(salaId); }
    @Override public List<SalaResponseDTO> listarPorCine(Long cineId) { return salaDAO.listarPorCine(cineId); }
}