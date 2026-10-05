package com.example.sistemaferreteriahardware.repository;

import com.example.sistemaferreteriahardware.models.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProducto_Id(Long productoId);
}
