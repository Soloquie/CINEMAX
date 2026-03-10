package com.uniquindio.CINEMAX.negocio.Model;
/**
 * Modelo de dominio para representar un rol de usuario en el sistema CINEMAX.
 * Este modelo se utiliza para almacenar y gestionar la información relacionada con los roles de usuario,
 * incluyendo su ID, nombre y descripción. Un rol es una categoría que se asigna a los usuarios para definir
 * sus permisos y responsabilidades dentro del sistema.
 */
public record Rol(
        Long id,
        String nombre,
        String descripcion
) {}