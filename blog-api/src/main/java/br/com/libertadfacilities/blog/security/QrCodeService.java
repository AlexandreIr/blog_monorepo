package br.com.libertadfacilities.blog.security;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class QrCodeService {

    public String generateQrCodeDataUrl(String email, String secret) {
        try {
            String issuer = "Blog LCS";
            String label = issuer + ":" + email;

            String otpauthUrl = "otpauth://totp/"
                    + URLEncoder.encode(label, StandardCharsets.UTF_8)
                    + "?secret=" + URLEncoder.encode(secret, StandardCharsets.UTF_8)
                    + "&issuer=" + URLEncoder.encode(issuer, StandardCharsets.UTF_8)
                    + "&algorithm=SHA1"
                    + "&digits=6"
                    + "&period=30";

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    otpauthUrl,
                    BarcodeFormat.QR_CODE,
                    260,
                    260
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar QR Code", e);
        }
    }
}
