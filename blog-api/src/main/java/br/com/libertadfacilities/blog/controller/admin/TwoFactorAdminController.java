package br.com.libertadfacilities.blog.controller.admin;

import br.com.libertadfacilities.blog.dto.request.ConfirmTwoFactorRequest;
import br.com.libertadfacilities.blog.dto.response.TwoFactorSetupResponse;
import br.com.libertadfacilities.blog.services.admin.TwoFactorAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/security/2fa")
@RequiredArgsConstructor
public class TwoFactorAdminController {

    private final TwoFactorAdminService twoFactorAdminService;

    @PostMapping("/setup")
    public ResponseEntity<TwoFactorSetupResponse> setup() {
        return ResponseEntity.ok(twoFactorAdminService.setup());
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@RequestBody @Valid ConfirmTwoFactorRequest request) {
        twoFactorAdminService.confirm(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> disable() {
        twoFactorAdminService.disable();
        return ResponseEntity.noContent().build();
    }
}
