package com.example.sistemaferreteriahardware.repository;

import com.example.sistemaferreteriahardware.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoria_Id(Long categoriaId);

    List<Producto> findByMarca_Id(Long marcaId);

    boolean existsByCategoria_Id(Long categoriaId);

    boolean existsByMarca_Id(Long marcaId);

    @Query("""
            SELECT p FROM Producto p
            JOIN p.inventario i
            WHERE i.stock <= i.stockMinimo
            ORDER BY i.stock ASC
            """)
    List<Producto> findConStockBajo();
}
