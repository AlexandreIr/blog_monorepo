package br.com.libertadfacilities.blog.services;


import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        return upload(file, "image", "blog-posts/images");
    }

    public String uploadVideo(MultipartFile file) {
        return upload(file, "video", "blog-posts/videos");
    }

    private String upload(MultipartFile file, String resourceType, String folder) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "folder", folder,
                            "resource_type", resourceType
                    )
            );

            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao enviar arquivo para o Cloudinary", e);
        }
    }
}
