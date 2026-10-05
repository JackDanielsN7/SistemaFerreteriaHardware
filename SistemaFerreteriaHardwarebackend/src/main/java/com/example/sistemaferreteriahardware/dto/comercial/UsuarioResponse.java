package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String usuario;
    private String estado;
    private Long rolId;
    private String rol;
}
