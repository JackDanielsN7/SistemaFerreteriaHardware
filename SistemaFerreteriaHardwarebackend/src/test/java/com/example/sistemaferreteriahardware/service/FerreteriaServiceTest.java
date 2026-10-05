package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.ClienteRequest;
import com.example.sistemaferreteriahardware.dto.comercial.DetalleVentaRequest;
import com.example.sistemaferreteriahardware.dto.comercial.ProductoRequest;
import com.example.sistemaferreteriahardware.dto.comercial.VentaRequest;
import com.example.sistemaferreteriahardware.exception.StockInsuficienteException;
import com.example.sistemaferreteriahardware.models.Categoria;
import com.example.sistemaferreteriahardware.models.Cliente;
import com.example.sistemaferreteriahardware.models.Inventario;
import com.example.sistemaferreteriahardware.models.Marca;
import com.example.sistemaferreteriahardware.models.Producto;
import com.example.sistemaferreteriahardware.models.Usuario;
import com.example.sistemaferreteriahardware.models.Venta;
import com.example.sistemaferreteriahardware.repository.DetalleVentaRepository;
import com.example.sistemaferreteriahardware.repository.InventarioRepository;
import com.example.sistemaferreteriahardware.repository.MovimientoInventarioRepository;
import com.example.sistemaferreteriahardware.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FerreteriaServiceTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private InventarioRepository inventarioRepository;
    @Mock private MovimientoInventarioRepository movimientoRepository;
    @Mock private DetalleVentaRepository detalleVentaRepository;
    @Mock private CategoriaService categoriaService;
    @Mock private MarcaService marcaService;
    @InjectMocks private ProductoService productoService;

    @Test
    void crearProducto() {
        when(categoriaService.obtener(1L)).thenReturn(categoria());
        when(marcaService.obtener(2L)).thenReturn(marca());
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto producto = inv.getArgument(0);
            producto.setId(5L);
            return producto;
        });
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = productoService.crear(productoRequest("SSD NVMe 1TB", 10));
        assertEquals("SSD NVMe 1TB", respuesta.getNombre());
        assertEquals(10, respuesta.getStock());
        assertEquals(new BigDecimal("300.00"), respuesta.getPrecioVenta());
    }

    @Test
    void buscarProductoPorNombre() {
        when(productoRepository.findByNombreContainingIgnoreCase("SSD")).thenReturn(List.of(producto(10)));
        var respuesta = productoService.buscar("SSD", null, null);
        assertEquals(1, respuesta.size());
        assertEquals("SSD NVMe 1TB", respuesta.get(0).getNombre());
    }

    @Test
    void actualizarProducto() {
        Producto producto = producto(10);
        when(productoRepository.findById(8L)).thenReturn(Optional.of(producto));
        when(categoriaService.obtener(1L)).thenReturn(producto.getCategoria());
        when(marcaService.obtener(2L)).thenReturn(producto.getMarca());
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoRequest request = productoRequest("SSD NVMe 2TB", 10);
        request.setPrecioVenta(new BigDecimal("450.00"));
        var respuesta = productoService.actualizar(8L, request);
        assertEquals("SSD NVMe 2TB", respuesta.getNombre());
        assertEquals(new BigDecimal("450.00"), respuesta.getPrecioVenta());
    }

    private ProductoRequest productoRequest(String nombre, int stock) {
        ProductoRequest request = new ProductoRequest();
        request.setNombre(nombre);
        request.setModelo("980 PRO");
        request.setPrecioCompra(new BigDecimal("180.00"));
        request.setPrecioVenta(new BigDecimal("300.00"));
        request.setCategoriaId(1L);
        request.setMarcaId(2L);
        request.setStockInicial(stock);
        request.setStockMinimo(2);
        request.setUbicacion("Estante A1");
        request.setEstado("ACTIVO");
        return request;
    }

    private Producto producto(int stock) {
        Producto producto = new Producto();
        producto.setId(8L);
        producto.setNombre("SSD NVMe 1TB");
        producto.setModelo("980 PRO");
        producto.setPrecioCompra(new BigDecimal("180.00"));
        producto.setPrecioVenta(new BigDecimal("300.00"));
        producto.setEstado("ACTIVO");
        producto.setCategoria(categoria());
        producto.setMarca(marca());
        Inventario inventario = new Inventario();
        inventario.setStock(stock);
        inventario.setStockMinimo(2);
        producto.setInventario(inventario);
        return producto;
    }

    private Categoria categoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Almacenamiento");
        return categoria;
    }

    private Marca marca() {
        Marca marca = new Marca();
        marca.setId(2L);
        marca.setNombre("Samsung");
        return marca;
    }
}

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {
    @Mock private com.example.sistemaferreteriahardware.repository.VentaRepository ventaRepository;
    @Mock private ClienteService clienteService;
    @Mock private UsuarioService usuarioService;
    @Mock private ProductoService productoService;
    @Mock private MovimientoInventarioService movimientoInventarioService;
    @InjectMocks private VentaService ventaService;

    @Test
    void registrarVentaCalculaTotal() {
        Producto producto = hardware(10);
        Usuario vendedor = usuario();
        when(clienteService.obtener(4L)).thenReturn(cliente());
        when(usuarioService.obtener(1L)).thenReturn(vendedor);
        when(productoService.obtener(8L)).thenReturn(producto);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> {
            Venta venta = inv.getArgument(0);
            venta.setId(9L);
            venta.getDetalles().forEach(detalle -> detalle.setId(1L));
            return venta;
        });

        var respuesta = ventaService.registrar(venta(2));
        assertEquals(new BigDecimal("600.00"), respuesta.getTotal());
        verify(movimientoInventarioService).registrarSalida(producto, vendedor, 2, "Venta 9");
    }

    @Test
    void stockInsuficienteNoRegistraVenta() {
        when(clienteService.obtener(4L)).thenReturn(cliente());
        when(usuarioService.obtener(1L)).thenReturn(usuario());
        when(productoService.obtener(8L)).thenReturn(hardware(1));
        assertThrows(StockInsuficienteException.class, () -> ventaService.registrar(venta(5)));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    void consultarVentasPorCliente() {
        Venta venta = new Venta();
        venta.setId(9L);
        venta.setFecha(java.time.LocalDateTime.now());
        venta.setTotal(new BigDecimal("600.00"));
        venta.setEstado("REGISTRADA");
        venta.setCliente(cliente());
        venta.setUsuario(usuario());
        venta.setDetalles(List.of());
        when(ventaRepository.findByCliente_IdOrderByFechaDesc(4L)).thenReturn(List.of(venta));
        assertEquals(1, ventaService.consultar(null, null, 4L, null).size());
    }

    private VentaRequest venta(int cantidad) {
        DetalleVentaRequest detalle = new DetalleVentaRequest();
        detalle.setProductoId(8L);
        detalle.setCantidad(cantidad);
        VentaRequest request = new VentaRequest();
        request.setClienteId(4L);
        request.setUsuarioId(1L);
        request.setDetalles(List.of(detalle));
        return request;
    }

    private Producto hardware(int stock) {
        Producto producto = new Producto();
        producto.setId(8L);
        producto.setNombre("SSD NVMe 1TB");
        producto.setEstado("ACTIVO");
        producto.setPrecioVenta(new BigDecimal("300.00"));
        Inventario inventario = new Inventario();
        inventario.setStock(stock);
        producto.setInventario(inventario);
        return producto;
    }

    private Cliente cliente() {
        Cliente cliente = new Cliente();
        cliente.setId(4L);
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        return cliente;
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Leo");
        usuario.setApellido("Vendedor");
        usuario.setUsuario("vendedor");
        usuario.setEstado("ACTIVO");
        return usuario;
    }
}

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    @Mock private com.example.sistemaferreteriahardware.repository.ClienteRepository clienteRepository;
    @Mock private com.example.sistemaferreteriahardware.repository.VentaRepository ventaRepository;
    @InjectMocks private ClienteService clienteService;

    @Test
    void registrarCliente() {
        when(clienteRepository.existsByDocumento("12345678")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente cliente = inv.getArgument(0);
            cliente.setId(4L);
            return cliente;
        });
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Juan");
        request.setApellido("Pérez");
        request.setDocumento("12345678");
        request.setCorreo("juan@ferreteria.com");
        var respuesta = clienteService.crear(request);
        assertEquals("12345678", respuesta.getDocumento());
        assertEquals("Pérez", respuesta.getApellido());
    }
}
