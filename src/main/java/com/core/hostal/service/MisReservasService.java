package com.core.hostal.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.dto.MisReservaClienteDTO;
import com.core.hostal.dto.ReservaResumenDTO;
import com.core.hostal.enums.EstadoReserva;
import com.core.hostal.repository.ReservaRepository;

@Service
@Transactional(readOnly = true)
public class MisReservasService {

    private final ReservaRepository reservaRepository;

    public MisReservasService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<MisReservaClienteDTO> listarPorCliente(Integer idCliente) {
        List<ReservaResumenDTO> reservas = reservaRepository.findResumenPorCliente(idCliente);
        List<MisReservaClienteDTO> resultado = new ArrayList<>();

        for (ReservaResumenDTO r : reservas) {
            boolean cancelable = r.getEstado() == EstadoReserva.pendiente
                || r.getEstado() == EstadoReserva.confirmada;
            resultado.add(new MisReservaClienteDTO(
                r.getIdReserva(),
                r.getCodigoReserva(),
                r.getFechaCheckin(),
                r.getFechaCheckout(),
                r.getNumHuespedes(),
                r.getTotal(),
                r.getEstado(),
                cancelable
            ));
        }

        resultado.sort(Comparator
            .comparing((MisReservaClienteDTO r) -> r.getEstado() == EstadoReserva.cancelada)
            .thenComparing(MisReservaClienteDTO::getFechaCheckin, Comparator.reverseOrder()));

        return resultado;
    }
}
