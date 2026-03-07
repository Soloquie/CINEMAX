package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.SeleccionAsientosDAO;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.CarritoService;
import com.uniquindio.CINEMAX.negocio.Service.SeleccionAsientosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeleccionAsientosServiceImpl implements SeleccionAsientosService {

    private final SeleccionAsientosDAO dao;
    private final CarritoService carritoService;


    @Override
    public List<FuncionAsientoResponseDTO> listarAsientos(Long funcionId, String emailUsuario) {
        return dao.listarAsientos(funcionId, emailUsuario);
    }

    @Override
    @Transactional
    public HoldAsientosResponseDTO hold(Long funcionId, String emailUsuario, HoldAsientosRequestDTO request) {
        HoldAsientosResponseDTO res = dao.hold(funcionId, emailUsuario, request);
        carritoService.addSeatHoldsToCart(emailUsuario, res.bloqueados(), res.expiraEn());

        return res;
    }

    @Override
    @Transactional
    public MessageResponseDTO release(Long funcionId, String emailUsuario, ReleaseAsientosRequestDTO request) {
        MessageResponseDTO res = dao.release(funcionId, emailUsuario, request);
        carritoService.removeSeatHoldsFromCart(emailUsuario, request.funcionAsientoIds());

        return res;
    }
}