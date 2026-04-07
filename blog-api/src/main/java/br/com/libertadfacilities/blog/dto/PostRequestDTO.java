package br.com.libertadfacilities.blog.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostRequestDTO {
    private String title;
    private String content;
    private String summary;
    private String metaTitle;
    private String metaDescription;
    private Long categoryId;
    private MultipartFile file;
}
