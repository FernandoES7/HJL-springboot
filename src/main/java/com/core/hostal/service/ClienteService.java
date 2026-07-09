package com.core.hostal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.dto.ClienteListadoDTO;
import com.core.hostal.entity.Cliente;
import com.core.hostal.enums.EstadoCliente;
import com.core.hostal.exception.OperacionInvalidaException;
import com.core.hostal.repository.ClienteRepository;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteListadoDTO> listarTodos() {
        return clienteRepository.findListado();
    }

    public List<ClienteListadoDTO> listarActivos() {
        return clienteRepository.findListadoPorEstado(EstadoCliente.activo);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public Optional<Cliente> buscarPorId(Integer idCliente) {
        return clienteRepository.findById(idCliente);
    }

    public long contarTotal() {
        return clienteRepository.count();
    }

    public List<ClienteListadoDTO> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarTodos();
        }
        String t = termino.trim().toLowerCase();
        return listarTodos().stream()
            .filter(c -> c.getNombre().toLowerCase().contains(t)
                || c.getEmail().toLowerCase().contains(t)
                || c.getDocumento().toLowerCase().contains(t)
                || (c.getTelefono() != null && c.getTelefono().contains(t)))
            .toList();
    }

    @Transactional
    public Cliente actualizar(Integer idCliente, String nombre, String telefono, EstadoCliente estado) {
        if (nombre == null || nombre.isBlank()) {
            throw new OperacionInvalidaException("El nombre es obligatorio");
        }
        Cliente cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() -> new OperacionInvalidaException("Cliente no encontrado"));
        cliente.setNombre(nombre.trim());
        cliente.setTelefono(telefono);
        cliente.setEstado(estado != null ? estado : cliente.getEstado());
        return clienteRepository.save(cliente);
    }
}
