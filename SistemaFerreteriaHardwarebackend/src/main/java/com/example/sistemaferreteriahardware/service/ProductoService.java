package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.ProductoRequest;
import com.example.sistemaferreteriahardware.dto.comercial.ProductoResponse;
import com.example.sistemaferreteriahardware.exception.RecursoNoEncontradoException;
import com.example.sistemaferreteriahardware.exception.ReglaNegocioException;
import com.example.sistemaferreteriahardware.models.Inventario;
import com.example.sistemaferreteriahardware.models.Producto;
import com.example.sistemaferreteriahardware.repository.DetalleVentaRepository;
import com.example.sistemaferreteriahardware.repository.InventarioRepository;
import com.example.sistemaferreteriahardware.repository.MovimientoInventarioRepository;
import com.example.sistemaferreteriahardware.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final CategoriaService categoriaService;
    private final MarcaService marcaService;

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto();
        aplicar(producto, request, true);
        Producto guardado = productoRepository.save(producto);
        Inventario inventario = new Inventario();
        inventario.setProducto(guardado);
        inventario.setStock(request.getStockInicial());
        inventario.setStockMinimo(request.getStockMinimo());
        inventario.setUbicacion(request.getUbicacion());
        inventarioRepository.save(inventario);
        guardado.setInventario(inventario);
        log.info("Producto creado: {}", guardado.getId());
        return map(guardado);
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscar(Long id) {
        return map(obtener(id));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> buscar(String nombre, Long categoriaId, Long marcaId) {
        List<Producto> productos;
        if (nombre != null && !nombre.isBlank()) {
            productos = productoRepository.findByNombreContainingIgnoreCase(nombre.trim());
        } else if (categoriaId != null) {
            productos = productoRepository.findByCategoria_Id(categoriaId);
        } else if (marcaId != null) {
            productos = productoRepository.findByMarca_Id(marcaId);
        } else {
            productos = productoRepository.findAll();
        }
        return productos.stream().map(this::map).toList();
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = obtener(id);
        aplicar(producto, request, false);
        if (producto.getInventario() != null) {
            producto.getInventario().setStockMinimo(request.getStockMinimo());
            if (request.getUbicacion() != null) {
                producto.getInventario().setUbicacion(request.getUbicacion());
            }
        }
        Producto guardado = productoRepository.save(producto);
        log.info("Producto actualizado: {}", guardado.getId());
        return map(guardado);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = obtener(id);
        if (detalleVentaRepository.existsByProducto_Id(id)) {
            throw new ReglaNegocioException("No se puede eliminar el producto porque tiene ventas asociadas");
        }
        movimientoRepository.deleteByProducto_Id(id);
        productoRepository.delete(producto);
        log.info("Producto eliminado: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> stockBajo() {
        return productoRepository.findConStockBajo().stream().map(this::map).toList();
    }

    public Producto obtener(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
    }

    private void aplicar(Producto producto, ProductoRequest request, boolean alta) {
        producto.setNombre(request.getNombre().trim());
        producto.setModelo(request.getModelo());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setCategoria(categoriaService.obtener(request.getCategoriaId()));
        producto.setMarca(marcaService.obtener(request.getMarcaId()));
        String estado = request.getEstado() == null || request.getEstado().isBlank() ? "ACTIVO" : request.getEstado().trim().toUpperCase();
        if (!estado.equals("ACTIVO") && !estado.equals("INACTIVO")) {
            throw new ReglaNegocioException("El estado del producto debe ser ACTIVO o INACTIVO");
        }
        producto.setEstado(estado);
        if (!alta && request.getStockInicial() != null && producto.getInventario() == null) {
            throw new ReglaNegocioException("El producto no tiene inventario");
        }
    }

    public ProductoResponse map(Producto producto) {
        Inventario inventario = producto.getInventario();
        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .modelo(producto.getModelo())
                .descripcion(producto.getDescripcion())
                .precioCompra(producto.getPrecioCompra())
                .precioVenta(producto.getPrecioVenta())
                .estado(producto.getEstado())
                .categoriaId(producto.getCategoria().getId())
                .categoriaNombre(producto.getCategoria().getNombre())
                .marcaId(producto.getMarca().getId())
                .marcaNombre(producto.getMarca().getNombre())
                .stock(inventario == null ? 0 : inventario.getStock())
                .stockMinimo(inventario == null ? 0 : inventario.getStockMinimo())
                .ubicacion(inventario == null ? null : inventario.getUbicacion())
                .build();
    }
}
