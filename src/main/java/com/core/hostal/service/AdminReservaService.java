package com.core.hostal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.entity.Reserva;
import com.core.hostal.enums.EstadoReserva;
import com.core.hostal.exception.ReservaException;
import com.core.hostal.repository.ReservaRepository;

@Service
public class AdminReservaService {

    private final ReservaRepository reservaRepository;

    public AdminReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Transactional
    public void cambiarEstado(Integer idReserva, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaRepository.findById(idReserva)
            .orElseThrow(() -> new ReservaException("Reserva no encontrada"));
        reserva.setEstado(nuevoEstado);
    }
}
