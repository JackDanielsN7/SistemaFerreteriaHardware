package com.example.sistemaferreteriahardware.service.impl;

import com.example.sistemaferreteriahardware.dto.JwtResponseDTO;
import com.example.sistemaferreteriahardware.dto.LoginRequestDTO;
import com.example.sistemaferreteriahardware.dto.RegisterRequestDTO;
import com.example.sistemaferreteriahardware.exception.ReglaNegocioException;
import com.example.sistemaferreteriahardware.models.Rol;
import com.example.sistemaferreteriahardware.models.Usuario;
import com.example.sistemaferreteriahardware.repository.UsuarioRepository;
import com.example.sistemaferreteriahardware.security.jwt.JwtUtils;
import com.example.sistemaferreteriahardware.service.AuthService;
import com.example.sistemaferreteriahardware.service.RolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolService rolService;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UsuarioRepository usuarioRepository,
                           RolService rolService,
                           PasswordEncoder encoder,
                           JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.rolService = rolService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public JwtResponseDTO loginUser(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsuario(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        Usuario userDetails = (Usuario) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();

        log.info("Inicio de sesión: {}", userDetails.getUsuario());

        return JwtResponseDTO.builder()
                .token(jwt)
                .id(userDetails.getId())
                .usuario(userDetails.getUsuario())
                .nombre(userDetails.getNombre())
                .apellido(userDetails.getApellido())
                .roles(roles)
                .build();
    }

    @Override
    public ResponseEntity<?> registerUser(RegisterRequestDTO registerRequest) {
        if (usuarioRepository.existsByUsuario(registerRequest.getUsuario())) {
            return ResponseEntity.badRequest().body("Error: El usuario ya está en uso.");
        }
        String rolNombre = registerRequest.getRol().trim().toUpperCase();
        if (!rolNombre.equals("ADMIN") && !rolNombre.equals("VENDEDOR")) {
            return ResponseEntity.badRequest().body("Error: El rol especificado no es válido.");
        }
        Rol rol;
        try {
            rol = rolService.obtenerPorNombre(rolNombre);
        } catch (RuntimeException ex) {
            throw new ReglaNegocioException("Rol no encontrado");
        }

        Usuario user = new Usuario();
        user.setUsuario(registerRequest.getUsuario().trim());
        user.setNombre(registerRequest.getNombre().trim());
        user.setApellido(registerRequest.getApellido().trim());
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        user.setEstado("ACTIVO");
        user.setRol(rol);
        usuarioRepository.save(user);
        log.info("Usuario registrado: {}", user.getUsuario());
        return ResponseEntity.ok("Usuario registrado exitosamente.");
    }
}
