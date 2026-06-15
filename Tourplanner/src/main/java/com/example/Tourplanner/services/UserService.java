package com.example.Tourplanner.services;

import com.example.Tourplanner.dto.UserRegisterRequestDTO;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void registerUser(UserRegisterRequestDTO dto) {
        if (usersRepository.findByUsername(dto.username()).isPresent()) {
            log.warn("Registration failed: username '{}' already exists", dto.username());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (usersRepository.findByEmail(dto.email()).isPresent()) {
            log.warn("Registration failed: email '{}' already exists", dto.email());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (!dto.password().equals(dto.passwordConfirmation())) {
            log.warn("Registration failed: password mismatch for user '{}'", dto.username());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password mismatch error");
        }

        Users user = new Users();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));

        usersRepository.save(user);
        log.info("User registered: username='{}', email='{}'", dto.username(), dto.email());
    }
}
