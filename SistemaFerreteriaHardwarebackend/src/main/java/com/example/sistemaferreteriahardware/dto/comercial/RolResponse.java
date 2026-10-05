package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RolResponse {
    private Long id;
    private String nombre;
    private String descripcion;
}
