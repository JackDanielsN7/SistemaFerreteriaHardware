package com.example.sistemaferreteriahardware.dto.comercial;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class VentaResponse {
    private Long id;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String estado;
    private Long clienteId;
    private String clienteNombre;
    private Long usuarioId;
    private String usuarioNombre;
    private List<DetalleVentaResponse> detalles;
}
