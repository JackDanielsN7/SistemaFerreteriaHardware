package com.example.sistemaferreteriahardware.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JwtResponseDTO {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String usuario;
    private String nombre;
    private String apellido;
    private List<String> roles;
}
