package br.com.libertadfacilities.blog.services;

import br.com.libertadfacilities.blog.exception.BusinessRuleException;
import br.com.libertadfacilities.blog.model.User;
import br.com.libertadfacilities.blog.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public Long createUser(User user){
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessRuleException("E-mail já cadastrado");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user).getId();
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new UsernameNotFoundException("Usuário não encontrado."));
    }
}
