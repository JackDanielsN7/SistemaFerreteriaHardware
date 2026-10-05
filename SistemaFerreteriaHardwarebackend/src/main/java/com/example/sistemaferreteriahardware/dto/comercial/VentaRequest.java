package com.example.sistemaferreteriahardware.dto.comercial;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VentaRequest {
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotEmpty(message = "La venta debe contener al menos un detalle")
    @Valid
    private List<DetalleVentaRequest> detalles;
}
