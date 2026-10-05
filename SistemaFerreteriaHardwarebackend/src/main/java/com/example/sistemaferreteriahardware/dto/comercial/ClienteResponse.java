package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClienteResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;
    private String correo;
}
