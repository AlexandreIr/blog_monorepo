package br.com.libertadfacilities.blog.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostRequestDTO {
    private String title;
    private String content;
    private Long categoryId;
    private MultipartFile file;
}
