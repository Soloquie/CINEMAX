package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.AsientoDAO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.AsientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsientoServiceImpl implements AsientoService {

    private final AsientoDAO asientoDAO;

    @Override public AsientosGenerarResponseDTO generar(Long salaId, AsientosGenerarDTO dto) { return asientoDAO.generar(salaId, dto); }
    @Override public List<AsientoResponseDTO> listarPorSala(Long salaId) { return asientoDAO.listarPorSala(salaId); }
}