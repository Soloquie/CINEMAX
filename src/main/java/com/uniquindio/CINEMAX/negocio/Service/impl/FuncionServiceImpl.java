package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.FuncionDAO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.FuncionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
//Clase implementacion de FuncionService para manejar la logica de las funciones
@Service
@RequiredArgsConstructor
public class FuncionServiceImpl implements FuncionService {

    private final FuncionDAO funcionDAO;

    //Sirve para crear la funcion
    @Override public FuncionResponseDTO crear(FuncionUpsertDTO dto) { return funcionDAO.crear(dto); }
    //Sirve para actualizar una funcion
    @Override public FuncionResponseDTO actualizar(Long id, FuncionUpsertDTO dto) { return funcionDAO.actualizar(id, dto); }
    //Sirve para cancelar la funcion
    @Override public FuncionResponseDTO cancelar(Long id) { return funcionDAO.cancelar(id); }
    //Sirve para listar todas las funciones con el rol de ADMIN
    @Override public List<FuncionResponseDTO> listarTodasAdmin() { return funcionDAO.listarTodas(); }
    //Sirve para listar las funciones por peliculas
    @Override public List<FuncionResponseDTO> listarProgramadasPorPelicula(Long peliculaId) { return funcionDAO.listarProgramadasPorPelicula(peliculaId); }
}