package com.example.Tourplanner.exceptions;

public class RouteCalculationException extends RuntimeException {
    public RouteCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
