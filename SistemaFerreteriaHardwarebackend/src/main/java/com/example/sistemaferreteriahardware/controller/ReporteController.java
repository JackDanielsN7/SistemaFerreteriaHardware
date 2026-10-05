package com.example.sistemaferreteriahardware.controller;

import com.example.sistemaferreteriahardware.dto.comercial.MovimientoResponse;
import com.example.sistemaferreteriahardware.dto.comercial.ProductoResponse;
import com.example.sistemaferreteriahardware.dto.comercial.ProductoVendidoResponse;
import com.example.sistemaferreteriahardware.dto.comercial.ReporteVentasPeriodoResponse;
import com.example.sistemaferreteriahardware.dto.comercial.TotalResponse;
import com.example.sistemaferreteriahardware.dto.comercial.VentaResponse;
import com.example.sistemaferreteriahardware.service.MovimientoInventarioService;
import com.example.sistemaferreteriahardware.service.ProductoService;
import com.example.sistemaferreteriahardware.service.ReporteService;
import com.example.sistemaferreteriahardware.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {
    private final ReporteService reporteService;
    private final ProductoService productoService;
    private final MovimientoInventarioService movimientoInventarioService;
    private final VentaService ventaService;

    @GetMapping("/ventas")
    public ResponseEntity<ReporteVentasPeriodoResponse> ventasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.ventasPorPeriodo(desde, hasta));
    }

    @GetMapping("/ventas/total")
    public ResponseEntity<TotalResponse> totalVentas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.totalVentas(desde, hasta));
    }

    @GetMapping("/ventas/cantidad")
    public ResponseEntity<TotalResponse> cantidadVentas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.totalVentas(desde, hasta));
    }

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ProductoVendidoResponse>> productosMasVendidos() {
        return ResponseEntity.ok(reporteService.productosMasVendidos());
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponse>> stockBajo() {
        return ResponseEntity.ok(productoService.stockBajo());
    }

    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoResponse>> movimientos(@RequestParam(required = false) Long productoId) {
        return ResponseEntity.ok(movimientoInventarioService.consultar(productoId));
    }

    @GetMapping("/ventas/usuario/{usuarioId}")
    public ResponseEntity<List<VentaResponse>> ventasPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ventaService.consultar(null, null, null, usuarioId));
    }

    @GetMapping("/ventas/cliente/{clienteId}")
    public ResponseEntity<List<VentaResponse>> ventasPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ventaService.consultar(null, null, clienteId, null));
    }
}
