package com.uniquindio.CINEMAX.negocio.Service.impl;



import com.uniquindio.CINEMAX.Persistencia.DAO.AsientoDAO;
import com.uniquindio.CINEMAX.Persistencia.Entity.TipoAsiento;
import com.uniquindio.CINEMAX.negocio.DTO.AsientoResponseDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarDTO;
import com.uniquindio.CINEMAX.negocio.DTO.AsientosGenerarResponseDTO;
import com.uniquindio.CINEMAX.negocio.Service.impl.AsientoServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsientoServiceImplTest {

    @Mock
    private AsientoDAO asientoDAO;

    @InjectMocks
    private AsientoServiceImpl asientoService;

    @Test
    void generarAsientos() {

        Long salaId = 1L;

        AsientosGenerarDTO dto = new AsientosGenerarDTO();

        AsientosGenerarResponseDTO response = new AsientosGenerarResponseDTO(
                salaId,
                10,
                0,
                0,
                10
        );

        when(asientoDAO.generar(salaId, dto)).thenReturn(response);

        AsientosGenerarResponseDTO result = asientoService.generar(salaId, dto);

        assertNotNull(result);
        assertEquals(salaId, result.salaId());

        verify(asientoDAO).generar(salaId, dto);
    }

    @Test
    void listarPorSala() {

        Long salaId = 1L;

        AsientoResponseDTO asiento = new AsientoResponseDTO(
                1L,
                "A",
                1,
                TipoAsiento.STANDARD,
                true
        );

        when(asientoDAO.listarPorSala(salaId)).thenReturn(List.of(asiento));

        List<AsientoResponseDTO> result = asientoService.listarPorSala(salaId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(asientoDAO).listarPorSala(salaId);
    }
}