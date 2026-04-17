package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.uniquindio.CINEMAX.negocio.Service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
/**
 * Implementación de la interfaz CloudinaryService para gestionar la subida de archivos a Cloudinary en el sistema CINEMAX.
 * Esta clase utiliza la biblioteca Cloudinary para interactuar con el servicio de almacenamiento en la nube y realizar las operaciones necesarias.
 */
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${app.cloudinary.folder:cinemax/posters}")
    private String folder;
    @Value("${app.cloudinary.products-folder:cinemax/products}")
    private String productFolder;


    /**
     * Implementación del método para subir un póster a Cloudinary. Este método recibe un archivo MultipartFile,
     * lo sube a Cloudinary y devuelve la URL segura del archivo subido.
     * @param file Archivo que se desea subir a Cloudinary. Debe ser un archivo de imagen válido.
     * @return URL segura del archivo subido a Cloudinary, o null si el archivo es nulo o está vacío.
     */
    @Override
    public String uploadPoster(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            Map<?, ?> res = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image"
                    )
            );
            return (String) res.get("secure_url");
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo subir el póster a Cloudinary", e);
        }
    }

    @Override
    public String uploadProductImage(MultipartFile file) {
        return uploadImage(file, productFolder, "No se pudo subir la imagen del producto a Cloudinary");
    }

    private String uploadImage(MultipartFile file, String folder, String errorMessage) {
        if (file == null || file.isEmpty()) return null;

        try {
            Map<?, ?> res = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image"
                    )
            );
            return (String) res.get("secure_url");
        } catch (Exception e) {
            throw new IllegalStateException(errorMessage, e);
        }
    }
}