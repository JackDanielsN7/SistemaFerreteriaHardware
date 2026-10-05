package com.example.sistemaferreteriahardware.repository;

import com.example.sistemaferreteriahardware.models.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    boolean existsByProducto_Id(Long productoId);

    @Query("""
            SELECT d.producto.id, d.producto.nombre, SUM(d.cantidad)
            FROM DetalleVenta d
            GROUP BY d.producto.id, d.producto.nombre
            ORDER BY SUM(d.cantidad) DESC
            """)
    List<Object[]> findProductosMasVendidos();
}
