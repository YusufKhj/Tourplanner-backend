package com.example.Tourplanner.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class APIResponse {

    public <T> ResponseEntity<Response<T>> success(T data, String message) {
        return ResponseEntity.ok(
                new Response<>(true, message, data, LocalDateTime.now())
        );
    }

    public ResponseEntity<Response<Object>> fail(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new Response<>(false, message, null, LocalDateTime.now()));
    }

    public record Response<T>(boolean success, String message, T data, LocalDateTime timestamp) {}
}