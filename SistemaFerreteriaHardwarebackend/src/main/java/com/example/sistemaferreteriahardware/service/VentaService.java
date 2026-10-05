package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.DetalleVentaResponse;
import com.example.sistemaferreteriahardware.dto.comercial.VentaRequest;
import com.example.sistemaferreteriahardware.dto.comercial.VentaResponse;
import com.example.sistemaferreteriahardware.exception.RecursoNoEncontradoException;
import com.example.sistemaferreteriahardware.exception.ReglaNegocioException;
import com.example.sistemaferreteriahardware.exception.StockInsuficienteException;
import com.example.sistemaferreteriahardware.models.Cliente;
import com.example.sistemaferreteriahardware.models.DetalleVenta;
import com.example.sistemaferreteriahardware.models.Producto;
import com.example.sistemaferreteriahardware.models.Usuario;
import com.example.sistemaferreteriahardware.models.Venta;
import com.example.sistemaferreteriahardware.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaService.class);
    private final VentaRepository ventaRepository;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final MovimientoInventarioService movimientoInventarioService;

    @Transactional
    public VentaResponse registrar(VentaRequest request) {
        Cliente cliente = clienteService.obtener(request.getClienteId());
        Usuario usuario = usuarioService.obtener(request.getUsuarioId());

        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (var detalle : request.getDetalles()) {
            cantidades.merge(detalle.getProductoId(), detalle.getCantidad(), Integer::sum);
        }

        Map<Long, Producto> productos = new LinkedHashMap<>();
        for (var entrada : cantidades.entrySet()) {
            Producto producto = productoService.obtener(entrada.getKey());
            if (!"ACTIVO".equalsIgnoreCase(producto.getEstado())) {
                throw new ReglaNegocioException("El producto " + producto.getNombre() + " no está activo");
            }
            int disponible = producto.getInventario() == null ? 0 : producto.getInventario().getStock();
            if (disponible < entrada.getValue()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto " + producto.getNombre());
            }
            productos.put(producto.getId(), producto);
        }

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        venta.setEstado("REGISTRADA");

        BigDecimal total = BigDecimal.ZERO;
        List<DetalleVenta> lineas = new ArrayList<>();
        for (var detalle : request.getDetalles()) {
            Producto producto = productos.get(detalle.getProductoId());
            BigDecimal subtotal = producto.getPrecioVenta().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            DetalleVenta linea = new DetalleVenta();
            linea.setVenta(venta);
            linea.setProducto(producto);
            linea.setCantidad(detalle.getCantidad());
            linea.setPrecioUnitario(producto.getPrecioVenta());
            linea.setSubtotal(subtotal);
            lineas.add(linea);
            total = total.add(subtotal);
        }
        venta.setTotal(total);
        venta.setDetalles(lineas);
        Venta guardada = ventaRepository.save(venta);

        for (var entrada : cantidades.entrySet()) {
            Producto producto = productos.get(entrada.getKey());
            movimientoInventarioService.registrarSalida(producto, usuario, entrada.getValue(), "Venta " + guardada.getId());
        }

        log.info("Venta registrada: {}", guardada.getId());
        return map(guardada);
    }

    @Transactional(readOnly = true)
    public VentaResponse buscar(Long id) {
        return map(obtener(id));
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> consultar(LocalDate desde, LocalDate hasta, Long clienteId, Long usuarioId) {
        List<Venta> ventas;
        if (clienteId != null) {
            ventas = ventaRepository.findByCliente_IdOrderByFechaDesc(clienteId);
        } else if (usuarioId != null) {
            ventas = ventaRepository.findByUsuario_IdOrderByFechaDesc(usuarioId);
        } else if (desde != null || hasta != null) {
            ventas = ventaRepository.findByFechaBetweenOrderByFechaDesc(inicio(desde, hasta), fin(desde, hasta));
        } else {
            ventas = ventaRepository.findAll();
        }
        return ventas.stream().map(this::map).toList();
    }

    public Venta obtener(Long id) {
        return ventaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));
    }

    public LocalDateTime inicio(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new ReglaNegocioException("Debe indicar la fecha desde y hasta");
        }
        if (hasta.isBefore(desde)) {
            throw new ReglaNegocioException("La fecha hasta no puede ser anterior a la fecha desde");
        }
        return desde.atStartOfDay();
    }

    public LocalDateTime fin(LocalDate desde, LocalDate hasta) {
        inicio(desde, hasta);
        return hasta.atTime(LocalTime.MAX);
    }

    public VentaResponse map(Venta venta) {
        List<DetalleVentaResponse> detalles = venta.getDetalles().stream()
                .map(detalle -> DetalleVentaResponse.builder()
                        .id(detalle.getId())
                        .productoId(detalle.getProducto().getId())
                        .productoNombre(detalle.getProducto().getNombre())
                        .cantidad(detalle.getCantidad())
                        .precioUnitario(detalle.getPrecioUnitario())
                        .subtotal(detalle.getSubtotal())
                        .build())
                .toList();
        return VentaResponse.builder()
                .id(venta.getId())
                .fecha(venta.getFecha())
                .total(venta.getTotal())
                .estado(venta.getEstado())
                .clienteId(venta.getCliente().getId())
                .clienteNombre(venta.getCliente().getNombre() + " " + venta.getCliente().getApellido())
                .usuarioId(venta.getUsuario().getId())
                .usuarioNombre(venta.getUsuario().getNombre() + " " + venta.getUsuario().getApellido())
                .detalles(detalles)
                .build();
    }
}
