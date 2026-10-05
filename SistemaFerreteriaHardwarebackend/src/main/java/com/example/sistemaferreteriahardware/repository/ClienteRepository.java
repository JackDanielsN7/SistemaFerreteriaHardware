package com.example.sistemaferreteriahardware.repository;

import com.example.sistemaferreteriahardware.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDocumento(String documento);
    boolean existsByDocumento(String documento);

    @Query("""
            SELECT c FROM Cliente c
            WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(CONCAT(c.nombre, ' ', c.apellido)) LIKE LOWER(CONCAT('%', :texto, '%'))
            """)
    List<Cliente> buscarPorNombre(@Param("texto") String texto);
}
