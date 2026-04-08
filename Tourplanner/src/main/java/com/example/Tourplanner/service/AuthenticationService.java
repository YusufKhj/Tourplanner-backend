package com.example.Tourplanner.service;

import com.example.Tourplanner.dto.UserLoginRequestDTO;
import com.example.Tourplanner.dto.UserLoginResponseDTO;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.repository.UsersRepository;
import com.example.Tourplanner.utils.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder encoder;
    private final JWT jwt;

    public Optional<UserLoginResponseDTO> authenticate(UserLoginRequestDTO request) {
        log.debug("Login attempt for username: {}", request.username());

        Optional<Users> userOpt = usersRepository.findByUsername(request.username());

        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", request.username());
            return Optional.empty();
        }

        Users user = userOpt.get();
        log.debug("User found: {}", user.getUsername());
        log.debug("Stored hash: {}", user.getPasswordHash());

        boolean passwordMatches = encoder.matches(request.password(), user.getPasswordHash());
        log.debug("Password matches: {}", passwordMatches);

        if (!passwordMatches) {
            log.warn("Wrong password for user: {}", request.username());
            return Optional.empty();
        }

        return Optional.of(createResponse(user));
    }

    private UserLoginResponseDTO createResponse(Users user) {
        String token = jwt.createToken(user.getUsername());
        return new UserLoginResponseDTO(user.getId(), user.getUsername(), user.getEmail(), token);
    }
}