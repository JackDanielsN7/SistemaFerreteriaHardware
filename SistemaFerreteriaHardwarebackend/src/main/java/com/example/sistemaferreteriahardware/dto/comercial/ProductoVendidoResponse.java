package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductoVendidoResponse {
    private Long productoId;
    private String nombre;
    private long cantidadVendida;
}
