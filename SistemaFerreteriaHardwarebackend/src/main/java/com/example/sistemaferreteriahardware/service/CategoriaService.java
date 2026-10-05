package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.CatalogoRequest;
import com.example.sistemaferreteriahardware.dto.comercial.CatalogoResponse;
import com.example.sistemaferreteriahardware.exception.RecursoNoEncontradoException;
import com.example.sistemaferreteriahardware.exception.ReglaNegocioException;
import com.example.sistemaferreteriahardware.models.Categoria;
import com.example.sistemaferreteriahardware.repository.CategoriaRepository;
import com.example.sistemaferreteriahardware.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public CatalogoResponse crear(CatalogoRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.getNombre().trim())) {
            throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre().trim());
        categoria.setDescripcion(request.getDescripcion());
        return map(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CatalogoResponse> listar() {
        return categoriaRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public CatalogoResponse buscar(Long id) {
        return map(obtener(id));
    }

    @Transactional
    public CatalogoResponse actualizar(Long id, CatalogoRequest request) {
        Categoria categoria = obtener(id);
        categoriaRepository.findByNombreIgnoreCase(request.getNombre().trim())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> { throw new ReglaNegocioException("Ya existe una categoría con ese nombre"); });
        categoria.setNombre(request.getNombre().trim());
        categoria.setDescripcion(request.getDescripcion());
        return map(categoriaRepository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        if (productoRepository.existsByCategoria_Id(id)) {
            throw new ReglaNegocioException("No se puede eliminar la categoría porque tiene productos");
        }
        categoriaRepository.delete(obtener(id));
    }

    public Categoria obtener(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));
    }

    private CatalogoResponse map(Categoria categoria) {
        return CatalogoResponse.builder().id(categoria.getId()).nombre(categoria.getNombre()).descripcion(categoria.getDescripcion()).build();
    }
}
