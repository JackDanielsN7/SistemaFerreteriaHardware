package com.example.sistemaferreteriahardware.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @NotBlank
    private String usuario;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    private String rol;
}
