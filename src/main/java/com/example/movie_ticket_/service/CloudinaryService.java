package com.example.movie_ticket_.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map upload(MultipartFile file) {
        try {
            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "movie_ticket_app", // 👈 folder tự động tạo
                            "use_filename", true,
                            "unique_filename", true,
                            "resource_type", "auto"));
        } catch (IOException e) {
            throw new RuntimeException("Upload error", e);
        }
    }
}
