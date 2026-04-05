package com.uniquindio.CINEMAX.negocio.Model;
/**
 * Modelo de dominio para representar un género de película en el sistema CINEMAX.
 * Este modelo se utiliza para almacenar y gestionar la información relacionada con los géneros de películas,
 * incluyendo su ID y nombre. El género es una categoría que se asigna a las películas para clasificarlas
 * según su contenido, estilo o temática.
 */
public record Genero(Long id, String nombre) {}