package com.core.hostal.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.core.hostal.dto.DisponibilidadTipoDTO;
import com.core.hostal.dto.TipoHabitacionAdminDTO;
import com.core.hostal.dto.TipoHabitacionCatalogoDTO;
import com.core.hostal.entity.TipoHabitacion;
import com.core.hostal.enums.EstadoHabitacion;
import com.core.hostal.enums.EstadoReserva;
import com.core.hostal.exception.OperacionInvalidaException;
import com.core.hostal.repository.HabitacionRepository;
import com.core.hostal.repository.TipoHabitacionRepository;

@Service
@Transactional(readOnly = true)
public class TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HabitacionRepository habitacionRepository;

    public TipoHabitacionService(
            TipoHabitacionRepository tipoHabitacionRepository,
            HabitacionRepository habitacionRepository) {
        this.tipoHabitacionRepository = tipoHabitacionRepository;
        this.habitacionRepository = habitacionRepository;
    }

    public List<TipoHabitacionAdminDTO> listarAdmin() {
        return tipoHabitacionRepository.findListadoAdmin();
    }

    public TipoHabitacion obtenerPorId(Integer idTipo) {
        return tipoHabitacionRepository.findById(idTipo)
            .orElseThrow(() -> new OperacionInvalidaException("Tipo de habitación no encontrado"));
    }

    @Transactional
    public TipoHabitacion crear(String nombre, String descripcion, Integer capacidad,
            BigDecimal precioBase, String imagenUrl, Integer cantidadTotal, Boolean activo) {
        validarDatos(nombre, capacidad, precioBase, cantidadTotal);
        if (tipoHabitacionRepository.existsByNombreIgnoreCase(nombre.trim())) {
            throw new OperacionInvalidaException("Ya existe un tipo de habitación con ese nombre");
        }
        TipoHabitacion tipo = new TipoHabitacion();
        tipo.setNombre(nombre.trim());
        tipo.setDescripcion(descripcion);
        tipo.setCapacidad(capacidad);
        tipo.setPrecioBase(precioBase);
        tipo.setImagenUrl(imagenUrl);
        tipo.setCantidadTotal(cantidadTotal);
        tipo.setActivo(activo != null ? activo : true);
        return tipoHabitacionRepository.save(tipo);
    }

    @Transactional
    public TipoHabitacion actualizar(Integer idTipo, String nombre, String descripcion,
            Integer capacidad, BigDecimal precioBase, String imagenUrl, Integer cantidadTotal,
            Boolean activo) {
        validarDatos(nombre, capacidad, precioBase, cantidadTotal);
        if (tipoHabitacionRepository.existsByNombreIgnoreCaseAndIdTipoNot(nombre.trim(), idTipo)) {
            throw new OperacionInvalidaException("Ya existe un tipo de habitación con ese nombre");
        }
        TipoHabitacion tipo = obtenerPorId(idTipo);
        tipo.setNombre(nombre.trim());
        tipo.setDescripcion(descripcion);
        tipo.setCapacidad(capacidad);
        tipo.setPrecioBase(precioBase);
        tipo.setImagenUrl(imagenUrl);
        tipo.setCantidadTotal(cantidadTotal);
        tipo.setActivo(activo != null ? activo : tipo.getActivo());
        return tipoHabitacionRepository.save(tipo);
    }

    @Transactional
    public void eliminar(Integer idTipo) {
        if (habitacionRepository.existsByTipoIdTipo(idTipo)) {
            throw new OperacionInvalidaException(
                "No se puede eliminar: existen habitaciones asociadas a este tipo");
        }
        TipoHabitacion tipo = obtenerPorId(idTipo);
        tipoHabitacionRepository.delete(tipo);
    }

    private void validarDatos(String nombre, Integer capacidad, BigDecimal precioBase, Integer cantidadTotal) {
        if (nombre == null || nombre.isBlank()) {
            throw new OperacionInvalidaException("El nombre es obligatorio");
        }
        if (capacidad == null || capacidad <= 0) {
            throw new OperacionInvalidaException("La capacidad debe ser mayor a 0");
        }
        if (precioBase == null || precioBase.signum() <= 0) {
            throw new OperacionInvalidaException("El precio base debe ser mayor a 0");
        }
        if (cantidadTotal == null || cantidadTotal <= 0) {
            throw new OperacionInvalidaException("La cantidad total debe ser mayor a 0");
        }
    }

    public List<TipoHabitacionCatalogoDTO> listarCatalogo() {
        return tipoHabitacionRepository.findCatalogoActivos();
    }

    public List<TipoHabitacionCatalogoDTO> listarCatalogoConFiltros(
            Integer idTipo, Integer capacidadMin, BigDecimal precioMax) {
        return tipoHabitacionRepository.findCatalogoConFiltros(idTipo, capacidadMin, precioMax);
    }

    public Optional<TipoHabitacionCatalogoDTO> obtenerCatalogo(Integer idTipo) {
        return tipoHabitacionRepository.findCatalogoById(idTipo);
    }

    public List<DisponibilidadTipoDTO> consultarDisponibilidad(
            LocalDate fechaCheckin, LocalDate fechaCheckout) {
        return tipoHabitacionRepository.findDisponibilidadPorFechas(
            fechaCheckin,
            fechaCheckout,
            null,
            EstadoHabitacion.disponible,
            EstadoReserva.cancelada,
            EstadoReserva.completada
        );
    }
}
