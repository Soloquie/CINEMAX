package com.uniquindio.CINEMAX.negocio.Service;

import org.springframework.web.multipart.MultipartFile;
/**
 * Interfaz de servicio para gestionar la integración con Cloudinary en el sistema CINEMAX.
 * Esta interfaz define el método necesario para subir carteles de películas a Cloudinary y obtener la URL resultante.
 */
public interface CloudinaryService {
    String uploadPoster(MultipartFile file);
    String uploadProductImage(MultipartFile file);
}