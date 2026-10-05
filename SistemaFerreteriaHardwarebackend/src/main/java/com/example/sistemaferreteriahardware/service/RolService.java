package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.RolRequest;
import com.example.sistemaferreteriahardware.dto.comercial.RolResponse;
import com.example.sistemaferreteriahardware.exception.RecursoNoEncontradoException;
import com.example.sistemaferreteriahardware.exception.ReglaNegocioException;
import com.example.sistemaferreteriahardware.models.Rol;
import com.example.sistemaferreteriahardware.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {
    private final RolRepository rolRepository;

    @Transactional
    public RolResponse crear(RolRequest request) {
        String nombre = request.getNombre().trim().toUpperCase();
        if (rolRepository.existsByNombre(nombre)) {
            throw new ReglaNegocioException("Ya existe un rol con ese nombre");
        }
        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setDescripcion(request.getDescripcion());
        return map(rolRepository.save(rol));
    }

    @Transactional(readOnly = true)
    public List<RolResponse> listar() {
        return rolRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public RolResponse buscar(Long id) {
        return map(obtener(id));
    }

    @Transactional
    public RolResponse actualizar(Long id, RolRequest request) {
        Rol rol = obtener(id);
        String nombre = request.getNombre().trim().toUpperCase();
        rolRepository.findByNombre(nombre)
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> { throw new ReglaNegocioException("Ya existe un rol con ese nombre"); });
        rol.setNombre(nombre);
        rol.setDescripcion(request.getDescripcion());
        return map(rolRepository.save(rol));
    }

    @Transactional
    public void eliminar(Long id) {
        Rol rol = obtener(id);
        if (rol.getUsuarios() != null && !rol.getUsuarios().isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar el rol porque tiene usuarios asignados");
        }
        rolRepository.delete(rol);
    }

    public Rol obtener(Long id) {
        return rolRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado"));
    }

    public Rol obtenerPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado"));
    }

    private RolResponse map(Rol rol) {
        return RolResponse.builder().id(rol.getId()).nombre(rol.getNombre()).descripcion(rol.getDescripcion()).build();
    }
}
