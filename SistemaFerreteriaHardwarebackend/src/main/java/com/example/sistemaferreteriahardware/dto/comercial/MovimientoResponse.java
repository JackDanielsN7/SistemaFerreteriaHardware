package com.example.sistemaferreteriahardware.dto.comercial;

import com.example.sistemaferreteriahardware.models.TipoMovimiento;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MovimientoResponse {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Long usuarioId;
    private String usuarioNombre;
    private TipoMovimiento tipoMovimiento;
    private Integer cantidad;
    private LocalDateTime fecha;
    private String motivo;
    private Integer stockResultante;
}
