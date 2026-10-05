package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.MovimientoRequest;
import com.example.sistemaferreteriahardware.dto.comercial.MovimientoResponse;
import com.example.sistemaferreteriahardware.exception.ReglaNegocioException;
import com.example.sistemaferreteriahardware.exception.StockInsuficienteException;
import com.example.sistemaferreteriahardware.models.Inventario;
import com.example.sistemaferreteriahardware.models.MovimientoInventario;
import com.example.sistemaferreteriahardware.models.Producto;
import com.example.sistemaferreteriahardware.models.TipoMovimiento;
import com.example.sistemaferreteriahardware.models.Usuario;
import com.example.sistemaferreteriahardware.repository.InventarioRepository;
import com.example.sistemaferreteriahardware.repository.MovimientoInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private static final Logger log = LoggerFactory.getLogger(MovimientoInventarioService.class);
    private final MovimientoInventarioRepository movimientoRepository;
    private final InventarioRepository inventarioRepository;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    @Transactional
    public MovimientoResponse registrar(MovimientoRequest request) {
        Producto producto = productoService.obtener(request.getProductoId());
        Usuario usuario = usuarioService.obtener(request.getUsuarioId());
        MovimientoInventario movimiento = aplicar(producto, usuario, request.getTipoMovimiento(), request.getCantidad(), request.getMotivo());
        return map(movimiento, producto.getInventario().getStock());
    }

    @Transactional
    public void registrarSalida(Producto producto, Usuario usuario, int cantidad, String motivo) {
        aplicar(producto, usuario, TipoMovimiento.SALIDA, cantidad, motivo);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> consultar(Long productoId) {
        List<MovimientoInventario> movimientos = productoId == null
                ? movimientoRepository.findAllByOrderByFechaDesc()
                : movimientoRepository.findByProducto_IdOrderByFechaDesc(productoId);
        return movimientos.stream().map(movimiento -> map(movimiento, stockDe(movimiento.getProducto()))).toList();
    }

    private MovimientoInventario aplicar(Producto producto, Usuario usuario, TipoMovimiento tipo, int cantidad, String motivo) {
        Inventario inventario = producto.getInventario();
        if (inventario == null) {
            throw new ReglaNegocioException("El producto no tiene inventario");
        }
        int stock = inventario.getStock();
        if (tipo == TipoMovimiento.ENTRADA) {
            stock += cantidad;
            log.info("Entrada de inventario: producto {} cantidad {}", producto.getId(), cantidad);
        } else {
            if (stock < cantidad) {
                throw new StockInsuficienteException("Stock insuficiente para el producto " + producto.getNombre());
            }
            stock -= cantidad;
            log.info("Salida de inventario: producto {} cantidad {}", producto.getId(), cantidad);
        }
        inventario.setStock(stock);
        inventarioRepository.save(inventario);
        log.info("Inventario actualizado: producto {} stock {}", producto.getId(), stock);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setMotivo(motivo);
        return movimientoRepository.save(movimiento);
    }

    private Integer stockDe(Producto producto) {
        return producto.getInventario() == null ? null : producto.getInventario().getStock();
    }

    private MovimientoResponse map(MovimientoInventario movimiento, Integer stock) {
        return MovimientoResponse.builder()
                .id(movimiento.getId())
                .productoId(movimiento.getProducto().getId())
                .productoNombre(movimiento.getProducto().getNombre())
                .usuarioId(movimiento.getUsuario().getId())
                .usuarioNombre(movimiento.getUsuario().getNombre() + " " + movimiento.getUsuario().getApellido())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .cantidad(movimiento.getCantidad())
                .fecha(movimiento.getFecha())
                .motivo(movimiento.getMotivo())
                .stockResultante(stock)
                .build();
    }
}
