package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.ProductoVendidoResponse;
import com.example.sistemaferreteriahardware.dto.comercial.ReporteVentasPeriodoResponse;
import com.example.sistemaferreteriahardware.dto.comercial.TotalResponse;
import com.example.sistemaferreteriahardware.repository.DetalleVentaRepository;
import com.example.sistemaferreteriahardware.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaService ventaService;

    @Transactional(readOnly = true)
    public ReporteVentasPeriodoResponse ventasPorPeriodo(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = ventaService.inicio(desde, hasta);
        LocalDateTime fin = ventaService.fin(desde, hasta);
        return ReporteVentasPeriodoResponse.builder()
                .desde(inicio)
                .hasta(fin)
                .total(ventaRepository.sumTotalEntre(inicio, fin))
                .cantidad(ventaRepository.contarEntre(inicio, fin))
                .ventas(ventaService.consultar(desde, hasta, null, null))
                .build();
    }

    @Transactional(readOnly = true)
    public TotalResponse totalVentas(LocalDate desde, LocalDate hasta) {
        if (desde == null && hasta == null) {
            return TotalResponse.builder().total(ventaRepository.sumTotal()).cantidad(ventaRepository.contar()).build();
        }
        LocalDateTime inicio = ventaService.inicio(desde, hasta);
        LocalDateTime fin = ventaService.fin(desde, hasta);
        return TotalResponse.builder()
                .total(ventaRepository.sumTotalEntre(inicio, fin))
                .cantidad(ventaRepository.contarEntre(inicio, fin))
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProductoVendidoResponse> productosMasVendidos() {
        return detalleVentaRepository.findProductosMasVendidos().stream()
                .limit(10)
                .map(fila -> ProductoVendidoResponse.builder()
                        .productoId(((Number) fila[0]).longValue())
                        .nombre((String) fila[1])
                        .cantidadVendida(((Number) fila[2]).longValue())
                        .build())
                .toList();
    }
}
