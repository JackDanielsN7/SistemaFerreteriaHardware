package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.CatalogoRequest;
import com.example.sistemaferreteriahardware.dto.comercial.ClienteRequest;
import com.example.sistemaferreteriahardware.dto.comercial.DetalleVentaRequest;
import com.example.sistemaferreteriahardware.dto.comercial.ProductoRequest;
import com.example.sistemaferreteriahardware.dto.comercial.VentaRequest;
import com.example.sistemaferreteriahardware.exception.StockInsuficienteException;
import com.example.sistemaferreteriahardware.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class FerreteriaIntegracionTest {

    @Autowired private ProductoService productoService;
    @Autowired private ClienteService clienteService;
    @Autowired private VentaService ventaService;
    @Autowired private CategoriaService categoriaService;
    @Autowired private MarcaService marcaService;
    @Autowired private UsuarioRepository usuarioRepository;

    @Test
    void ventaDescuentaStockYSePuedeConsultar() {
        Long categoriaId = categoria();
        Long marcaId = marca();
        var producto = productoService.crear(producto("RTX " + sufijo(), categoriaId, marcaId, 10));
        var cliente = clienteService.crear(cliente());
        Long usuarioId = usuarioRepository.findByUsuario("vendedor").orElseThrow().getId();

        var venta = ventaService.registrar(venta(cliente.getId(), usuarioId, producto.getId(), 2));
        assertEquals(new BigDecimal("600.00"), venta.getTotal());
        assertEquals(8, productoService.buscar(producto.getId()).getStock());
        assertEquals(1, ventaService.consultar(null, null, cliente.getId(), null).size());
    }

    @Test
    void stockInsuficienteConservaInventario() {
        var producto = productoService.crear(producto("RAM " + sufijo(), categoria(), marca(), 1));
        var cliente = clienteService.crear(cliente());
        Long usuarioId = usuarioRepository.findByUsuario("vendedor").orElseThrow().getId();
        assertThrows(StockInsuficienteException.class,
                () -> ventaService.registrar(venta(cliente.getId(), usuarioId, producto.getId(), 4)));
        assertEquals(1, productoService.buscar(producto.getId()).getStock());
    }

    private Long categoria() {
        CatalogoRequest request = new CatalogoRequest();
        request.setNombre("Cat " + sufijo());
        return categoriaService.crear(request).getId();
    }

    private Long marca() {
        CatalogoRequest request = new CatalogoRequest();
        request.setNombre("Marca " + sufijo());
        return marcaService.crear(request).getId();
    }

    private ProductoRequest producto(String nombre, Long categoriaId, Long marcaId, int stock) {
        ProductoRequest request = new ProductoRequest();
        request.setNombre(nombre);
        request.setModelo("X");
        request.setPrecioCompra(new BigDecimal("180.00"));
        request.setPrecioVenta(new BigDecimal("300.00"));
        request.setCategoriaId(categoriaId);
        request.setMarcaId(marcaId);
        request.setStockInicial(stock);
        request.setStockMinimo(1);
        request.setUbicacion("A1");
        request.setEstado("ACTIVO");
        return request;
    }

    private ClienteRequest cliente() {
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setDocumento(documento());
        request.setCorreo(sufijo() + "@ferreteria.com");
        return request;
    }

    private VentaRequest venta(Long clienteId, Long usuarioId, Long productoId, int cantidad) {
        DetalleVentaRequest detalle = new DetalleVentaRequest();
        detalle.setProductoId(productoId);
        detalle.setCantidad(cantidad);
        VentaRequest request = new VentaRequest();
        request.setClienteId(clienteId);
        request.setUsuarioId(usuarioId);
        request.setDetalles(List.of(detalle));
        return request;
    }

    private String sufijo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String documento() {
        return String.valueOf(10_000_000 + ThreadLocalRandom.current().nextInt(80_000_000));
    }
}
