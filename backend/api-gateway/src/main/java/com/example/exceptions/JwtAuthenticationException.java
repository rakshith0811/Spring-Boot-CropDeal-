package com.example.exceptions;

public class JwtAuthenticationException extends Exception {
    public JwtAuthenticationException(String message) {
        super(message);
    }
}
