package com.uniquindio.CINEMAX.negocio.Service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.uniquindio.CINEMAX.negocio.Service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${app.cloudinary.folder:cinemax/posters}")
    private String folder;

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
}