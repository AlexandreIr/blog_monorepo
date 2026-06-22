package br.com.libertadfacilities.blog.dto.response;


public record TwoFactorSetupResponse(
        String secret,
        String qrCodeDataUrl
) {
}