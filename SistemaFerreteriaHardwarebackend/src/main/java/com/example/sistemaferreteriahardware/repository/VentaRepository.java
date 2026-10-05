package com.example.sistemaferreteriahardware.repository;

import com.example.sistemaferreteriahardware.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByFechaBetweenOrderByFechaDesc(LocalDateTime desde, LocalDateTime hasta);

    List<Venta> findByCliente_IdOrderByFechaDesc(Long clienteId);

    List<Venta> findByUsuario_IdOrderByFechaDesc(Long usuarioId);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v")
    BigDecimal sumTotal();

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta")
    BigDecimal sumTotalEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COUNT(v) FROM Venta v")
    long contar();

    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta")
    long contarEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    boolean existsByCliente_Id(Long clienteId);

    boolean existsByUsuario_Id(Long usuarioId);
}
