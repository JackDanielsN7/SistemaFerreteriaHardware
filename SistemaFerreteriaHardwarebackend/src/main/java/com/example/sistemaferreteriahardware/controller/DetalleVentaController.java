package com.example.sistemaferreteriahardware.controller;

import com.example.sistemaferreteriahardware.dto.comercial.DetalleVentaResponse;
import com.example.sistemaferreteriahardware.service.DetalleVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-venta")
@RequiredArgsConstructor
public class DetalleVentaController {
    private final DetalleVentaService detalleVentaService;

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<DetalleVentaResponse>> porVenta(@PathVariable Long ventaId) {
        return ResponseEntity.ok(detalleVentaService.listarPorVenta(ventaId));
    }
}
