package com.core.hostal.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.entity.Cancelacion;
import com.core.hostal.entity.Reserva;
import com.core.hostal.enums.EstadoReserva;
import com.core.hostal.exception.ReservaException;
import com.core.hostal.repository.CancelacionRepository;
import com.core.hostal.repository.ReservaRepository;

@Service
public class CancelacionService {

    private final ReservaRepository reservaRepository;
    private final CancelacionRepository cancelacionRepository;

    public CancelacionService(
            ReservaRepository reservaRepository,
            CancelacionRepository cancelacionRepository) {
        this.reservaRepository = reservaRepository;
        this.cancelacionRepository = cancelacionRepository;
    }

    @Transactional
    public void cancelarPorCliente(Integer idReserva, Integer idCliente, String motivo) {
        Reserva reserva = reservaRepository.findByIdReservaAndClienteIdCliente(idReserva, idCliente)
            .orElseThrow(() -> new ReservaException("Reserva no encontrada."));

        if (reserva.getEstado() == EstadoReserva.cancelada) {
            throw new ReservaException("La reserva ya está cancelada.");
        }
        if (reserva.getEstado() == EstadoReserva.completada) {
            throw new ReservaException("No se puede cancelar una reserva completada.");
        }
        if (cancelacionRepository.existsByReservaIdReserva(idReserva)) {
            throw new ReservaException("La reserva ya tiene una cancelación registrada.");
        }

        Cancelacion cancelacion = new Cancelacion();
        cancelacion.setReserva(reserva);
        cancelacion.setMotivo(motivo != null && !motivo.isBlank() ? motivo.trim() : "Cancelada por el cliente");
        cancelacion.setFecha(LocalDate.now());
        cancelacion.setEmpleado(null);

        reserva.setEstado(EstadoReserva.cancelada);
        reserva.setCancelacion(cancelacion);
        reservaRepository.save(reserva);
    }
}
