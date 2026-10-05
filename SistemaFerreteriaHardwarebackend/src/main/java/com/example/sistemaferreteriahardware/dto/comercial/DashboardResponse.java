package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DashboardResponse {
    private BigDecimal totalVentas;
    private long cantidadVentas;
    private long productos;
    private long clientes;
    private long stockBajo;
    private List<ProductoVendidoResponse> masVendidos;
    private List<ProductoResponse> productosStockBajo;
}
