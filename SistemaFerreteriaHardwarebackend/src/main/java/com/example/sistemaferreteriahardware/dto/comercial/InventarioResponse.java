package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventarioResponse {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private String modelo;
    private Integer stock;
    private Integer stockMinimo;
    private String ubicacion;
}
