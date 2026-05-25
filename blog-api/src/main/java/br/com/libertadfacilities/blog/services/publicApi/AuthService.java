package br.com.libertadfacilities.blog.services.publicApi;


import br.com.libertadfacilities.blog.dto.request.AuthRequest;
import br.com.libertadfacilities.blog.dto.response.AuthResponse;
import br.com.libertadfacilities.blog.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse login (AuthRequest authRequest) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.email(),
                        authRequest.password()
                )
        );

        UserDetails user = (UserDetails) authentication.getPrincipal();
        assert user != null;
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, "Bearer");
    }


}
