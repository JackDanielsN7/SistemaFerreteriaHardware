package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String modelo;
    private String descripcion;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private String estado;
    private Long categoriaId;
    private String categoriaNombre;
    private Long marcaId;
    private String marcaNombre;
    private Integer stock;
    private Integer stockMinimo;
    private String ubicacion;
}
