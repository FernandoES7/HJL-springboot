package com.core.hostal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.dto.HabitacionReservadaDTO;
import com.core.hostal.dto.ReservaDetalleDTO;
import com.core.hostal.dto.ReservaResumenDTO;
import com.core.hostal.entity.Reserva;
import com.core.hostal.enums.EstadoReserva;
import com.core.hostal.repository.ReservaHabitacionRepository;
import com.core.hostal.repository.ReservaRepository;

@Service
@Transactional(readOnly = true)
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaHabitacionRepository reservaHabitacionRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            ReservaHabitacionRepository reservaHabitacionRepository) {
        this.reservaRepository = reservaRepository;
        this.reservaHabitacionRepository = reservaHabitacionRepository;
    }

    public List<ReservaResumenDTO> listarTodas() {
        return reservaRepository.findResumenTodas();
    }

    public List<ReservaResumenDTO> listarPorCliente(Integer idCliente) {
        return reservaRepository.findResumenPorCliente(idCliente);
    }

    public List<ReservaResumenDTO> listarPorEstado(EstadoReserva estado) {
        return reservaRepository.findResumenPorEstado(estado);
    }

    public List<ReservaResumenDTO> buscar(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarTodas();
        }
        String t = termino.trim().toLowerCase();
        return listarTodas().stream()
            .filter(r -> r.getCodigoReserva().toLowerCase().contains(t)
                || r.getNombreCliente().toLowerCase().contains(t)
                || String.valueOf(r.getIdReserva()).contains(t))
            .toList();
    }

    public long contarTotal() {
        return reservaRepository.count();
    }

    public long contarConfirmadas() {
        return reservaRepository.countByEstado(EstadoReserva.confirmada);
    }

    public Optional<ReservaDetalleDTO> obtenerDetalle(Integer idReserva) {
        return reservaRepository.findByIdConCliente(idReserva)
            .map(this::construirDetalle);
    }

    public Optional<ReservaDetalleDTO> obtenerDetallePorCodigo(String codigoReserva) {
        return reservaRepository.findByCodigoConCliente(codigoReserva)
            .map(this::construirDetalle);
    }

    private ReservaDetalleDTO construirDetalle(Reserva reserva) {
        List<HabitacionReservadaDTO> habitaciones =
            reservaHabitacionRepository.findHabitacionesPorReserva(reserva.getIdReserva());

        return new ReservaDetalleDTO(
            reserva.getIdReserva(),
            reserva.getCodigoReserva(),
            reserva.getCliente().getNombre(),
            reserva.getCliente().getEmail(),
            reserva.getFechaReserva(),
            reserva.getFechaCheckin(),
            reserva.getFechaCheckout(),
            reserva.getNumHuespedes(),
            reserva.getTotal(),
            reserva.getEstado(),
            reserva.getFechaCheckinReal(),
            reserva.getFechaCheckoutReal(),
            habitaciones
        );
    }
}
