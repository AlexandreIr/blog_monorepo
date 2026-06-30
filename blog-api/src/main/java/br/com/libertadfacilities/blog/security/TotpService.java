package br.com.libertadfacilities.blog.security;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;

@Service
public class TotpService {

    private static final int SECRET_SIZE = 20;
    private static final int CODE_DIGITS = 6;
    private static final int TIME_STEP_SECONDS = 30;
    private static final int WINDOW = 1;

    public String generateSecret() {
        byte[] bytes = new byte[SECRET_SIZE];
        new SecureRandom().nextBytes(bytes);

        Base32 base32 = new Base32();
        return base32.encodeToString(bytes).replace("=", "");
    }

    public boolean verifyCode(String secret, String code) {
        if (secret == null || secret.isBlank() || code == null || !code.matches("\\d{6}")) {
            return false;
        }

        long currentTimeStep = Instant.now().getEpochSecond() / TIME_STEP_SECONDS;

        for (int i = -WINDOW; i <= WINDOW; i++) {
            String expectedCode = generateCode(secret, currentTimeStep + i);

            if (constantTimeEquals(expectedCode, code)) {
                return true;
            }
        }

        return false;
    }

    private String generateCode(String secret, long timeStep) {
        try {
            Base32 base32 = new Base32();
            byte[] key = base32.decode(secret);

            byte[] data = new byte[8];
            long value = timeStep;

            for (int i = 7; i >= 0; i--) {
                data[i] = (byte) (value & 0xff);
                value >>= 8;
            }

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));

            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xf;

            int binary =
                    ((hash[offset] & 0x7f) << 24)
                            | ((hash[offset + 1] & 0xff) << 16)
                            | ((hash[offset + 2] & 0xff) << 8)
                            | (hash[offset + 3] & 0xff);

            int otp = binary % (int) Math.pow(10, CODE_DIGITS);

            return String.format("%0" + CODE_DIGITS + "d", otp);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar código TOTP", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }

        int result = 0;

        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }

        return result == 0;
    }
}