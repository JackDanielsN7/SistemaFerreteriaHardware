package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CatalogoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
}
