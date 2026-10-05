package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.DashboardResponse;
import com.example.sistemaferreteriahardware.repository.ClienteRepository;
import com.example.sistemaferreteriahardware.repository.ProductoRepository;
import com.example.sistemaferreteriahardware.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoService productoService;
    private final ReporteService reporteService;

    @Transactional(readOnly = true)
    public DashboardResponse resumen() {
        var stockBajo = productoService.stockBajo();
        return DashboardResponse.builder()
                .totalVentas(ventaRepository.sumTotal())
                .cantidadVentas(ventaRepository.contar())
                .productos(productoRepository.count())
                .clientes(clienteRepository.count())
                .stockBajo(stockBajo.size())
                .masVendidos(reporteService.productosMasVendidos())
                .productosStockBajo(stockBajo)
                .build();
    }
}
