package com.example.sistemaferreteriahardware.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    @JsonAlias("username")
    private String usuario;

    @NotBlank
    private String password;
}
