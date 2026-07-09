package com.core.hostal.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.dto.HabitacionListadoDTO;
import com.core.hostal.entity.Habitacion;
import com.core.hostal.entity.TipoHabitacion;
import com.core.hostal.enums.EstadoHabitacion;
import com.core.hostal.enums.EstadoReserva;
import com.core.hostal.exception.OperacionInvalidaException;
import com.core.hostal.repository.HabitacionRepository;
import com.core.hostal.repository.HotelRepository;
import com.core.hostal.repository.ReservaHabitacionRepository;
import com.core.hostal.repository.TipoHabitacionRepository;

@Service
@Transactional(readOnly = true)
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HotelRepository hotelRepository;
    private final ReservaHabitacionRepository reservaHabitacionRepository;

    public HabitacionService(
            HabitacionRepository habitacionRepository,
            TipoHabitacionRepository tipoHabitacionRepository,
            HotelRepository hotelRepository,
            ReservaHabitacionRepository reservaHabitacionRepository) {
        this.habitacionRepository = habitacionRepository;
        this.tipoHabitacionRepository = tipoHabitacionRepository;
        this.hotelRepository = hotelRepository;
        this.reservaHabitacionRepository = reservaHabitacionRepository;
    }

    public List<HabitacionListadoDTO> listarPorHotel(Integer idHotel) {
        return habitacionRepository.findListadoPorHotel(idHotel);
    }

    public List<HabitacionListadoDTO> buscarPorHotel(Integer idHotel, String termino, Integer idTipo) {
        List<HabitacionListadoDTO> base = (idTipo != null)
            ? habitacionRepository.findListadoPorHotelYTipo(idHotel, idTipo)
            : listarPorHotel(idHotel);

        if (termino == null || termino.isBlank()) {
            return base;
        }
        String t = termino.trim().toLowerCase();
        return base.stream()
            .filter(h -> h.getNumero().toLowerCase().contains(t)
                || h.getNombreTipo().toLowerCase().contains(t)
                || String.valueOf(h.getPiso()).contains(t))
            .toList();
    }

    public long contarPorHotel(Integer idHotel) {
        return listarPorHotel(idHotel).size();
    }

    public long contarDisponiblesPorHotel(Integer idHotel) {
        return listarPorHotel(idHotel).stream()
            .filter(h -> h.getEstado() == EstadoHabitacion.disponible)
            .count();
    }

    public long contarOcupadasPorHotel(Integer idHotel) {
        return listarPorHotel(idHotel).stream()
            .filter(h -> h.getEstado() == EstadoHabitacion.ocupada)
            .count();
    }

    public List<Habitacion> buscarDisponibles(
            Integer idHotel,
            Integer idTipo,
            LocalDate fechaCheckin,
            LocalDate fechaCheckout) {
        return habitacionRepository.findDisponiblesPorTipoYFechas(
            idHotel,
            idTipo,
            fechaCheckin,
            fechaCheckout,
            EstadoHabitacion.disponible,
            EstadoReserva.cancelada,
            EstadoReserva.completada
        );
    }

    public Habitacion obtenerPorId(Integer idHabitacion) {
        return habitacionRepository.findById(idHabitacion)
            .orElseThrow(() -> new OperacionInvalidaException("Habitación no encontrada"));
    }

    @Transactional
    public Habitacion crear(Integer idHotel, String numero, Integer piso, Integer idTipo, EstadoHabitacion estado) {
        validarDatos(numero, piso);
        if (habitacionRepository.existsByHotelIdHotelAndNumero(idHotel, numero.trim())) {
            throw new OperacionInvalidaException("Ya existe una habitación con ese número");
        }
        TipoHabitacion tipo = tipoHabitacionRepository.findById(idTipo)
            .orElseThrow(() -> new OperacionInvalidaException("Tipo de habitación no encontrado"));

        Habitacion habitacion = new Habitacion();
        habitacion.setHotel(hotelRepository.getReferenceById(idHotel));
        habitacion.setTipo(tipo);
        habitacion.setNumero(numero.trim());
        habitacion.setPiso(piso);
        habitacion.setEstado(estado != null ? estado : EstadoHabitacion.disponible);
        return habitacionRepository.save(habitacion);
    }

    @Transactional
    public Habitacion actualizar(Integer idHabitacion, String numero, Integer piso, Integer idTipo, EstadoHabitacion estado) {
        validarDatos(numero, piso);
        Habitacion habitacion = obtenerPorId(idHabitacion);

        if (!habitacion.getNumero().equalsIgnoreCase(numero.trim())
                && habitacionRepository.existsByHotelIdHotelAndNumero(habitacion.getHotel().getIdHotel(), numero.trim())) {
            throw new OperacionInvalidaException("Ya existe una habitación con ese número");
        }
        TipoHabitacion tipo = tipoHabitacionRepository.findById(idTipo)
            .orElseThrow(() -> new OperacionInvalidaException("Tipo de habitación no encontrado"));

        habitacion.setNumero(numero.trim());
        habitacion.setPiso(piso);
        habitacion.setTipo(tipo);
        habitacion.setEstado(estado != null ? estado : habitacion.getEstado());
        return habitacionRepository.save(habitacion);
    }

    @Transactional
    public void eliminar(Integer idHabitacion) {
        Habitacion habitacion = obtenerPorId(idHabitacion);
        if (reservaHabitacionRepository.existsByHabitacionIdHabitacion(idHabitacion)) {
            throw new OperacionInvalidaException(
                "No se puede eliminar: la habitación tiene reservas asociadas");
        }
        habitacionRepository.delete(habitacion);
    }

    private void validarDatos(String numero, Integer piso) {
        if (numero == null || numero.isBlank()) {
            throw new OperacionInvalidaException("El número de habitación es obligatorio");
        }
        if (piso == null || piso < 0) {
            throw new OperacionInvalidaException("El piso debe ser un número válido");
        }
    }
}
