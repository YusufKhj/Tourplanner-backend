
package com.example.Tourplanner.service;

import com.example.Tourplanner.dto.UserRequestRegisterDTO;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserRequestRegisterDTO dto) {
        // Prüfen, ob Username oder Email existiert
        if (usersRepository.findByUsername(dto.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (usersRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Entity erzeugen
        Users user = new Users();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));

        // User speichern
        usersRepository.save(user);
    }
}