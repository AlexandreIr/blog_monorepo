package br.com.libertadfacilities.blog.dto.response;

public record LoginResponse (
        String token,
        String type,
        boolean requiresTwoFactor,
        String temporaryToken
) {
}
