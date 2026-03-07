package com.uniquindio.CINEMAX.negocio.Service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadPoster(MultipartFile file);
}