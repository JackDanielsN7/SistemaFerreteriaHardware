package com.example.sistemaferreteriahardware.service;

import com.example.sistemaferreteriahardware.dto.comercial.ClienteRequest;
import com.example.sistemaferreteriahardware.dto.comercial.ClienteResponse;
import com.example.sistemaferreteriahardware.exception.RecursoNoEncontradoException;
import com.example.sistemaferreteriahardware.exception.ReglaNegocioException;
import com.example.sistemaferreteriahardware.models.Cliente;
import com.example.sistemaferreteriahardware.repository.ClienteRepository;
import com.example.sistemaferreteriahardware.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.existsByDocumento(request.getDocumento().trim())) {
            throw new ReglaNegocioException("Ya existe un cliente con ese documento");
        }
        Cliente cliente = new Cliente();
        aplicar(cliente, request);
        Cliente guardado = clienteRepository.save(cliente);
        log.info("Cliente registrado: {}", guardado.getId());
        return map(guardado);
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) {
        return map(obtener(id));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> buscar(String documento, String nombre) {
        if (documento != null && !documento.isBlank()) {
            return clienteRepository.findByDocumento(documento.trim()).map(cliente -> List.of(map(cliente))).orElse(List.of());
        }
        if (nombre != null && !nombre.isBlank()) {
            return clienteRepository.buscarPorNombre(nombre.trim()).stream().map(this::map).toList();
        }
        return clienteRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = obtener(id);
        clienteRepository.findByDocumento(request.getDocumento().trim())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> { throw new ReglaNegocioException("Ya existe un cliente con ese documento"); });
        aplicar(cliente, request);
        return map(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        if (ventaRepository.existsByCliente_Id(id)) {
            throw new ReglaNegocioException("No se puede eliminar el cliente porque tiene ventas");
        }
        clienteRepository.delete(obtener(id));
    }

    public Cliente obtener(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
    }

    private void aplicar(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.getNombre().trim());
        cliente.setApellido(request.getApellido().trim());
        cliente.setDocumento(request.getDocumento().trim());
        cliente.setTelefono(request.getTelefono());
        cliente.setCorreo(request.getCorreo().trim());
    }

    public ClienteResponse map(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .documento(cliente.getDocumento())
                .telefono(cliente.getTelefono())
                .correo(cliente.getCorreo())
                .build();
    }
}
