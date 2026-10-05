package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.InventarioResponse;
import com.example.sistemaferreteriahardware.exception.ReglaNegocioException;
import com.example.sistemaferreteriahardware.models.Inventario;
import com.example.sistemaferreteriahardware.models.Producto;
import com.example.sistemaferreteriahardware.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {
    private final InventarioRepository inventarioRepository;
    private final ProductoService productoService;

    @Transactional(readOnly = true)
    public List<InventarioResponse> listar() {
        return inventarioRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public InventarioResponse buscarPorProducto(Long productoId) {
        Producto producto = productoService.obtener(productoId);
        if (producto.getInventario() == null) {
            throw new ReglaNegocioException("El producto no tiene inventario");
        }
        return map(producto.getInventario());
    }

    private InventarioResponse map(Inventario inventario) {
        return InventarioResponse.builder()
                .id(inventario.getId())
                .productoId(inventario.getProducto().getId())
                .productoNombre(inventario.getProducto().getNombre())
                .modelo(inventario.getProducto().getModelo())
                .stock(inventario.getStock())
                .stockMinimo(inventario.getStockMinimo())
                .ubicacion(inventario.getUbicacion())
                .build();
    }
}
