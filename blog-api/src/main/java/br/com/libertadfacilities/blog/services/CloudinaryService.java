package br.com.libertadfacilities.blog.services;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file){
        try{
            var uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e){
            throw new RuntimeException("Erro ao fazer upload do arquivo");
        }
    }
}
