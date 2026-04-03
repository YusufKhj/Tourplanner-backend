package com.example.Tourplanner.service;

import com.example.Tourplanner.dto.UserLoginRequestDTO;
import com.example.Tourplanner.entities.Users;
import com.example.Tourplanner.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Tourplanner.utils.JWT;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    UsersRepository usersRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    JWT jwt;

    public String loginUser(UserLoginRequestDTO dto) {

        Users user = usersRepository.findByUsername(dto.username()).orElseThrow(()-> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password"
        ));
        System.out.println("User found: " + user);
        if (passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            return jwt.generateToken(user.getUsername());
        }
        return null;
    }
}
