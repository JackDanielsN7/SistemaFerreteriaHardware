package com.example.sistemaferreteriahardware.service;


import com.example.sistemaferreteriahardware.dto.JwtResponseDTO;
import com.example.sistemaferreteriahardware.dto.LoginRequestDTO;
import com.example.sistemaferreteriahardware.dto.RegisterRequestDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    JwtResponseDTO loginUser(LoginRequestDTO loginRequest);
    ResponseEntity<?> registerUser(RegisterRequestDTO registerRequest);
}