package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.uniquindio.CINEMAX.Persistencia.DAO.SeleccionAsientosDAO;
import com.uniquindio.CINEMAX.negocio.DTO.*;
import com.uniquindio.CINEMAX.negocio.Service.CarritoService;
import com.uniquindio.CINEMAX.negocio.Service.SeleccionAsientosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Implementación de la interfaz SeleccionAsientosService para gestionar la selección de asientos en el sistema CINEMAX.
 * Esta clase utiliza un DAO para interactuar con la base de datos y un servicio de carrito para gestionar los asientos bloqueados en el carrito del usuario.
 */
@Service
@RequiredArgsConstructor
public class SeleccionAsientosServiceImpl implements SeleccionAsientosService {
    /* Implementación del método para listar los asientos disponibles para una función específica. */
    private final SeleccionAsientosDAO dao;
    private final CarritoService carritoService;

    /**
     * Implementación del método para listar los asientos disponibles para una función específica.
     * @param funcionId ID de la función para la cual se desean listar los asientos.
     * @param emailUsuario Correo electrónico del usuario que realiza la consulta,
     * utilizado para identificar los asientos bloqueados en su carrito.
     * @return Lista de DTOs de respuesta que representan los asientos disponibles para la función
     */
    @Override
    public List<FuncionAsientoResponseDTO> listarAsientos(Long funcionId, String emailUsuario) {
        return dao.listarAsientos(funcionId, emailUsuario);
    }
    /**
     * Implementación del método para bloquear asientos para una función específica. Este método maneja la lógica
     * de bloqueo de asientos en la base de datos a través del DAO,
     * y luego agrega los asientos bloqueados al carrito del usuario utilizando el servicio de carrito.
     * @param funcionId ID de la función para la cual se desean bloquear los asientos.
     * @param emailUsuario Correo electrónico del usuario que realiza la solicitud de bloqueo,
     * utilizado para identificar los asientos bloqueados en su carrito.
     * @param request DTO de solicitud que contiene la información necesaria para bloquear los asientos,
     * como los IDs de los asientos a bloquear.
     * @return DTO de respuesta que representa el resultado del bloqueo de asientos,
     * incluyendo los IDs de los asientos bloqueados y el tiempo de expiración del bloqueo.
     */
    @Override
    @Transactional
    public HoldAsientosResponseDTO hold(Long funcionId, String emailUsuario, HoldAsientosRequestDTO request) {
        HoldAsientosResponseDTO res = dao.hold(funcionId, emailUsuario, request);
        carritoService.addSeatHoldsToCart(emailUsuario, res.bloqueados(), res.expiraEn());

        return res;
    }
    /**
     * Implementación del método para liberar asientos bloqueados para una función específica. Este método maneja la lógica
     * de liberación de asientos en la base de datos a través del DAO,
     * y luego elimina los asientos liberados del carrito del usuario utilizando el servicio de carrito.
     * @param funcionId ID de la función para la cual se desean liberar los asientos bloqueados.
     * @param emailUsuario Correo electrónico del usuario que realiza la solicitud de liberación,
     * utilizado para identificar los asientos bloqueados en su carrito.
     * @param request DTO de solicitud que contiene la información necesaria para liberar los asientos,
     * como los IDs de los asientos a liberar.
     * @return DTO de respuesta que representa el resultado de la liberación de asientos,
     * incluyendo un mensaje que indica si la operación fue exitosa o si hubo algún error.
     */
    @Override
    @Transactional
    public MessageResponseDTO release(Long funcionId, String emailUsuario, ReleaseAsientosRequestDTO request) {
        MessageResponseDTO res = dao.release(funcionId, emailUsuario, request);
        carritoService.removeSeatHoldsFromCart(emailUsuario, request.funcionAsientoIds());

        return res;
    }
}