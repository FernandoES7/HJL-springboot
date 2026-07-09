package com.core.hostal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.dto.FacturaResumenDTO;
import com.core.hostal.entity.Factura;
import com.core.hostal.enums.EstadoFactura;
import com.core.hostal.repository.FacturaRepository;

@Service
@Transactional(readOnly = true)
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<FacturaResumenDTO> listarTodas() {
        return facturaRepository.findResumenTodas();
    }

    public List<FacturaResumenDTO> listarPorEstado(EstadoFactura estado) {
        return facturaRepository.findResumenPorEstado(estado);
    }

    public List<FacturaResumenDTO> listarPorCliente(Integer idCliente) {
        return facturaRepository.findResumenPorCliente(idCliente);
    }

    public Optional<Factura> buscarPorReserva(Integer idReserva) {
        return facturaRepository.findByReservaIdReserva(idReserva);
    }
}
