package br.com.libertadfacilities.blog.controller.publicApi;

import br.com.libertadfacilities.blog.dto.request.AuthRequest;
import br.com.libertadfacilities.blog.dto.response.AuthResponse;
import br.com.libertadfacilities.blog.entity.User;
import br.com.libertadfacilities.blog.services.publicApi.AuthService;
import br.com.libertadfacilities.blog.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody User user){
        Long newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
