package com.example.sistemaferreteriahardware.config;

import com.example.sistemaferreteriahardware.models.Categoria;
import com.example.sistemaferreteriahardware.models.Inventario;
import com.example.sistemaferreteriahardware.models.Marca;
import com.example.sistemaferreteriahardware.models.Producto;
import com.example.sistemaferreteriahardware.models.Rol;
import com.example.sistemaferreteriahardware.models.Usuario;
import com.example.sistemaferreteriahardware.repository.CategoriaRepository;
import com.example.sistemaferreteriahardware.repository.InventarioRepository;
import com.example.sistemaferreteriahardware.repository.MarcaRepository;
import com.example.sistemaferreteriahardware.repository.ProductoRepository;
import com.example.sistemaferreteriahardware.repository.RolRepository;
import com.example.sistemaferreteriahardware.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Rol admin = seedRol("ADMIN", "Administra usuarios, catálogo y reportes");
        Rol vendedor = seedRol("VENDEDOR", "Registra ventas y consulta inventario");
        seedUsuario("admin", "Ada", "Admin", admin, "admin123");
        seedUsuario("vendedor", "Leo", "Vendedor", vendedor, "vendedor123");

        if (productoRepository.count() == 0) {
            Categoria almacenamiento = seedCategoria("Almacenamiento", "Discos SSD y HDD");
            Categoria memoria = seedCategoria("Memoria RAM", "Módulos de memoria");
            Categoria video = seedCategoria("Tarjetas de video", "GPUs para escritorio");
            Marca kingston = seedMarca("Kingston", "Memorias y almacenamiento");
            Marca samsung = seedMarca("Samsung", "SSD y componentes");
            Marca nvidia = seedMarca("NVIDIA", "Tarjetas gráficas");
            crearProducto("SSD NVMe 1TB", "980 PRO", "Unidad NVMe PCIe 4.0", "180.00", "300.00", almacenamiento, samsung, 12, 3, "Estante A1");
            crearProducto("RAM DDR4 16GB", "Fury Beast", "3200 MHz CL16", "140.00", "250.00", memoria, kingston, 20, 4, "Estante B2");
            crearProducto("RTX 4060", "Dual", "Tarjeta de video 8GB GDDR6", "980.00", "1400.00", video, nvidia, 6, 2, "Vitrina C1");
        }
    }

    private Rol seedRol(String nombre, String descripcion) {
        Rol rol = rolRepository.findByNombre(nombre).orElse(new Rol());
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);
        return rolRepository.save(rol);
    }

    private Usuario seedUsuario(String usuario, String nombre, String apellido, Rol rol, String password) {
        Usuario u = usuarioRepository.findByUsuario(usuario).orElse(new Usuario());
        u.setUsuario(usuario);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setRol(rol);
        u.setEstado("ACTIVO");
        u.setPassword(passwordEncoder.encode(password));
        return usuarioRepository.save(u);
    }

    private Categoria seedCategoria(String nombre, String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        return categoriaRepository.save(categoria);
    }

    private Marca seedMarca(String nombre, String descripcion) {
        Marca marca = new Marca();
        marca.setNombre(nombre);
        marca.setDescripcion(descripcion);
        return marcaRepository.save(marca);
    }

    private void crearProducto(String nombre, String modelo, String descripcion, String compra, String venta,
                               Categoria categoria, Marca marca, int stock, int minimo, String ubicacion) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setModelo(modelo);
        producto.setDescripcion(descripcion);
        producto.setPrecioCompra(new BigDecimal(compra));
        producto.setPrecioVenta(new BigDecimal(venta));
        producto.setEstado("ACTIVO");
        producto.setCategoria(categoria);
        producto.setMarca(marca);
        Producto guardado = productoRepository.save(producto);
        Inventario inventario = new Inventario();
        inventario.setProducto(guardado);
        inventario.setStock(stock);
        inventario.setStockMinimo(minimo);
        inventario.setUbicacion(ubicacion);
        inventarioRepository.save(inventario);
    }
}
