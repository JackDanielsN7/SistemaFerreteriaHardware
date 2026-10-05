package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.DetalleVentaResponse;
import com.example.sistemaferreteriahardware.models.DetalleVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetalleVentaService {
    private final VentaService ventaService;

    @Transactional(readOnly = true)
    public List<DetalleVentaResponse> listarPorVenta(Long ventaId) {
        return ventaService.obtener(ventaId).getDetalles().stream().map(this::map).toList();
    }

    private DetalleVentaResponse map(DetalleVenta detalle) {
        return DetalleVentaResponse.builder()
                .id(detalle.getId())
                .productoId(detalle.getProducto().getId())
                .productoNombre(detalle.getProducto().getNombre())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}
