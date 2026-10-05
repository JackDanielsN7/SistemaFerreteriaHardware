package com.example.sistemaferreteriahardware.repository;

import com.example.sistemaferreteriahardware.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
}
