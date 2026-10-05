package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.CatalogoRequest;
import com.example.sistemaferreteriahardware.dto.comercial.CatalogoResponse;
import com.example.sistemaferreteriahardware.exception.RecursoNoEncontradoException;
import com.example.sistemaferreteriahardware.exception.ReglaNegocioException;
import com.example.sistemaferreteriahardware.models.Marca;
import com.example.sistemaferreteriahardware.repository.MarcaRepository;
import com.example.sistemaferreteriahardware.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarcaService {
    private final MarcaRepository marcaRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public CatalogoResponse crear(CatalogoRequest request) {
        if (marcaRepository.existsByNombreIgnoreCase(request.getNombre().trim())) {
            throw new ReglaNegocioException("Ya existe una marca con ese nombre");
        }
        Marca marca = new Marca();
        marca.setNombre(request.getNombre().trim());
        marca.setDescripcion(request.getDescripcion());
        return map(marcaRepository.save(marca));
    }

    @Transactional(readOnly = true)
    public List<CatalogoResponse> listar() {
        return marcaRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public CatalogoResponse buscar(Long id) {
        return map(obtener(id));
    }

    @Transactional
    public CatalogoResponse actualizar(Long id, CatalogoRequest request) {
        Marca marca = obtener(id);
        marcaRepository.findByNombreIgnoreCase(request.getNombre().trim())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> { throw new ReglaNegocioException("Ya existe una marca con ese nombre"); });
        marca.setNombre(request.getNombre().trim());
        marca.setDescripcion(request.getDescripcion());
        return map(marcaRepository.save(marca));
    }

    @Transactional
    public void eliminar(Long id) {
        if (productoRepository.existsByMarca_Id(id)) {
            throw new ReglaNegocioException("No se puede eliminar la marca porque tiene productos");
        }
        marcaRepository.delete(obtener(id));
    }

    public Marca obtener(Long id) {
        return marcaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Marca no encontrada"));
    }

    private CatalogoResponse map(Marca marca) {
        return CatalogoResponse.builder().id(marca.getId()).nombre(marca.getNombre()).descripcion(marca.getDescripcion()).build();
    }
}
