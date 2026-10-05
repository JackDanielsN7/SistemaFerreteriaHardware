package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReporteVentasPeriodoResponse {
    private LocalDateTime desde;
    private LocalDateTime hasta;
    private BigDecimal total;
    private long cantidad;
    private List<VentaResponse> ventas;
}
