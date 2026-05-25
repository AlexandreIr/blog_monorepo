package br.com.libertadfacilities.blog.dto.response;

public record AuthResponse (
    String token,
    String type
){}
