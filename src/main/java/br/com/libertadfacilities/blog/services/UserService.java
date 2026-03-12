package br.com.libertadfacilities.blog.services;

import br.com.libertadfacilities.blog.model.User;
import br.com.libertadfacilities.blog.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user){
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        //TODO definir contrato de criptografia da senha do usuário

        return userRepository.save(user);
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Usuário não encontrado."));
    }
}
