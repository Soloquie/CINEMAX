package com.uniquindio.CINEMAX.negocio.DTO;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PeliculaUpsertFormDTO {
    private String titulo;
    private String sinopsis;
    private Integer duracionMin;
    private String clasificacion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaEstreno;

    private Boolean activa = true;

    private List<Long> generoIds = new ArrayList<>();

    private MultipartFile poster; // opcional
}