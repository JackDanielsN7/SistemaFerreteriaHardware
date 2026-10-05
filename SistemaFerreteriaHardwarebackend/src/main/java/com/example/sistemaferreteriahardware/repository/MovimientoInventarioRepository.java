package com.example.sistemaferreteriahardware.repository;

import com.example.sistemaferreteriahardware.models.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findAllByOrderByFechaDesc();

    List<MovimientoInventario> findByProducto_IdOrderByFechaDesc(Long productoId);

    void deleteByProducto_Id(Long productoId);
}
