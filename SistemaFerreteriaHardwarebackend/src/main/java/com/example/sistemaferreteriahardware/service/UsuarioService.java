package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.EstadoRequest;
import com.example.sistemaferreteriahardware.dto.comercial.UsuarioRequest;
import com.example.sistemaferreteriahardware.dto.comercial.UsuarioResponse;
import com.example.sistemaferreteriahardware.exception.RecursoNoEncontradoException;
import com.example.sistemaferreteriahardware.exception.ReglaNegocioException;
import com.example.sistemaferreteriahardware.models.Rol;
import com.example.sistemaferreteriahardware.models.Usuario;
import com.example.sistemaferreteriahardware.repository.UsuarioRepository;
import com.example.sistemaferreteriahardware.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;
    private final RolService rolService;
    private final VentaRepository ventaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ReglaNegocioException("La contraseña es obligatoria");
        }
        if (usuarioRepository.existsByUsuario(request.getUsuario().trim())) {
            throw new ReglaNegocioException("El usuario ya está en uso");
        }
        Usuario usuario = new Usuario();
        aplicar(usuario, request, true);
        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado: {}", guardado.getId());
        return map(guardado);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return map(obtener(id));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = obtener(id);
        if (usuarioRepository.existsByUsuario(request.getUsuario().trim())
                && !usuario.getUsuario().equals(request.getUsuario().trim())) {
            throw new ReglaNegocioException("El usuario ya está en uso");
        }
        aplicar(usuario, request, false);
        return map(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, EstadoRequest request) {
        Usuario usuario = obtener(id);
        usuario.setEstado(request.getEstado());
        log.info("Usuario {} estado {}", id, request.getEstado());
        return map(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtener(id);
        if (ventaRepository.existsByUsuario_Id(id) || !usuario.getMovimientos().isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar el usuario porque tiene operaciones registradas");
        }
        usuarioRepository.delete(usuario);
    }

    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    private void aplicar(Usuario usuario, UsuarioRequest request, boolean alta) {
        Rol rol = rolService.obtener(request.getRolId());
        usuario.setNombre(request.getNombre().trim());
        usuario.setApellido(request.getApellido().trim());
        usuario.setUsuario(request.getUsuario().trim());
        usuario.setRol(rol);
        String estado = request.getEstado() == null || request.getEstado().isBlank() ? "ACTIVO" : request.getEstado();
        usuario.setEstado(estado);
        if (alta || (request.getPassword() != null && !request.getPassword().isBlank())) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    private UsuarioResponse map(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .usuario(usuario.getUsuario())
                .estado(usuario.getEstado())
                .rolId(usuario.getRol() == null ? null : usuario.getRol().getId())
                .rol(usuario.getRol() == null ? null : usuario.getRol().getNombre())
                .build();
    }
}
