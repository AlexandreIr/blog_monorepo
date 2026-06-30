package br.com.libertadfacilities.blog.services.admin;


import br.com.libertadfacilities.blog.dto.request.ConfirmTwoFactorRequest;
import br.com.libertadfacilities.blog.dto.response.TwoFactorSetupResponse;
import br.com.libertadfacilities.blog.entity.User;
import br.com.libertadfacilities.blog.exception.BusinessRuleException;
import br.com.libertadfacilities.blog.exception.ResourceNotFoundException;
import br.com.libertadfacilities.blog.repositories.UserRepository;
import br.com.libertadfacilities.blog.security.QrCodeService;
import br.com.libertadfacilities.blog.security.TotpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TwoFactorAdminService {

    private final UserRepository userRepository;
    private final TotpService totpService;
    private final QrCodeService qrCodeService;

    public TwoFactorSetupResponse setup() {
        User user = getAuthenticatedUser();

        String secret = totpService.generateSecret();
        String qrCode = qrCodeService.generateQrCodeDataUrl(user.getEmail(), secret);

        user.setTwoFactorPendingSecret(secret);
        userRepository.save(user);

        return new TwoFactorSetupResponse(secret, qrCode);
    }

    public void confirm(ConfirmTwoFactorRequest request) {
        User user = getAuthenticatedUser();

        String pendingSecret = user.getTwoFactorPendingSecret();

        if (pendingSecret == null || pendingSecret.isBlank()) {
            throw new BusinessRuleException("Nenhuma configuração 2FA pendente");
        }

        if (!totpService.verifyCode(pendingSecret, request.code())) {
            throw new BusinessRuleException("Código 2FA inválido");
        }

        user.setTwoFactorSecret(pendingSecret);
        user.setTwoFactorPendingSecret(null);
        user.setTwoFactorEnabled(true);

        userRepository.save(user);
    }

    public void disable() {
        User user = getAuthenticatedUser();

        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        user.setTwoFactorPendingSecret(null);

        userRepository.save(user);
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }
}
