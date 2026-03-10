package com.uniquindio.CINEMAX.negocio.DTO;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 * DTO (Data Transfer Object) para representar la información necesaria para crear o actualizar una película en el
 * sistema CINEMAX. Este DTO se utiliza para recibir los datos de una película desde el cliente, incluyendo su título,
 * sinopsis, duración en minutos, clasificación, fecha de estreno, estado de actividad, IDs de géneros asociados y un archivo opcional para el póster.
 */
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