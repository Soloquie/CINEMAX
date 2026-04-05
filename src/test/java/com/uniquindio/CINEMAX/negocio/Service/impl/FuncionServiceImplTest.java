package com.uniquindio.CINEMAX.negocio.Service.impl;





import com.uniquindio.CINEMAX.Persistencia.DAO.FuncionDAO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.FuncionUpsertDTO;
import com.uniquindio.CINEMAX.negocio.Service.impl.FuncionServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionServiceImplTest {

    @Mock
    private FuncionDAO funcionDAO;

    @InjectMocks
    private FuncionServiceImpl funcionService;

    @Test
    void crearFuncion() {

        FuncionUpsertDTO dto = new FuncionUpsertDTO(
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                "ES",
                false,
                BigDecimal.valueOf(15000)
        );

        FuncionResponseDTO response = new FuncionResponseDTO(
                1L,
                1L,
                "Avengers",
                "poster.jpg",
                1L,
                "Sala 1",
                1L,
                "Cinemax Armenia",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                "ES",
                false,
                BigDecimal.valueOf(15000),
                "PROGRAMADA"
        );

        when(funcionDAO.crear(dto)).thenReturn(response);

        FuncionResponseDTO result = funcionService.crear(dto);

        assertNotNull(result);
        verify(funcionDAO).crear(dto);
    }

    @Test
    void actualizarFuncion() {

        Long id = 1L;

        FuncionUpsertDTO dto = new FuncionUpsertDTO(
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                "ES",
                false,
                BigDecimal.valueOf(15000)
        );

        FuncionResponseDTO response = new FuncionResponseDTO(
                id,
                1L,
                "Avengers",
                "poster.jpg",
                1L,
                "Sala 1",
                1L,
                "Cinemax Armenia",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                "ES",
                false,
                BigDecimal.valueOf(15000),
                "PROGRAMADA"
        );

        when(funcionDAO.actualizar(id, dto)).thenReturn(response);

        FuncionResponseDTO result = funcionService.actualizar(id, dto);

        assertNotNull(result);
        verify(funcionDAO).actualizar(id, dto);
    }

    @Test
    void cancelarFuncion() {

        Long id = 1L;

        FuncionResponseDTO response = new FuncionResponseDTO(
                id,
                1L,
                "Avengers",
                "poster.jpg",
                1L,
                "Sala 1",
                1L,
                "Cinemax Armenia",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                "ES",
                false,
                BigDecimal.valueOf(15000),
                "CANCELADA"
        );

        when(funcionDAO.cancelar(id)).thenReturn(response);

        FuncionResponseDTO result = funcionService.cancelar(id);

        assertNotNull(result);
        verify(funcionDAO).cancelar(id);
    }

    @Test
    void listarTodasAdmin() {

        FuncionResponseDTO funcion = new FuncionResponseDTO(
                1L,
                1L,
                "Avengers",
                "poster.jpg",
                1L,
                "Sala 1",
                1L,
                "Cinemax Armenia",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                "ES",
                false,
                BigDecimal.valueOf(15000),
                "PROGRAMADA"
        );

        when(funcionDAO.listarTodas()).thenReturn(List.of(funcion));

        List<FuncionResponseDTO> result = funcionService.listarTodasAdmin();

        assertFalse(result.isEmpty());
        verify(funcionDAO).listarTodas();
    }

    @Test
    void listarProgramadasPorPelicula() {

        Long peliculaId = 1L;

        FuncionResponseDTO funcion = new FuncionResponseDTO(
                1L,
                peliculaId,
                "Avengers",
                "poster.jpg",
                1L,
                "Sala 1",
                1L,
                "Cinemax Armenia",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                "ES",
                false,
                BigDecimal.valueOf(15000),
                "PROGRAMADA"
        );

        when(funcionDAO.listarProgramadasPorPelicula(peliculaId))
                .thenReturn(List.of(funcion));

        List<FuncionResponseDTO> result = funcionService.listarProgramadasPorPelicula(peliculaId);

        assertFalse(result.isEmpty());
        verify(funcionDAO).listarProgramadasPorPelicula(peliculaId);
    }
}