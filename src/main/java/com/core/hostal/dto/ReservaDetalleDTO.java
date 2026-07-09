package com.core.hostal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.core.hostal.enums.EstadoReserva;

public class ReservaDetalleDTO {

    private Integer idReserva;
    private String codigoReserva;
    private String nombreCliente;
    private String emailCliente;
    private LocalDate fechaReserva;
    private LocalDate fechaCheckin;
    private LocalDate fechaCheckout;
    private Integer numHuespedes;
    private BigDecimal total;
    private EstadoReserva estado;
    private LocalDateTime fechaCheckinReal;
    private LocalDateTime fechaCheckoutReal;
    private List<HabitacionReservadaDTO> habitaciones;

    public ReservaDetalleDTO() {
    }

    public ReservaDetalleDTO(Integer idReserva, String codigoReserva, String nombreCliente,
            String emailCliente, LocalDate fechaReserva, LocalDate fechaCheckin,
            LocalDate fechaCheckout, Integer numHuespedes, BigDecimal total,
            EstadoReserva estado, LocalDateTime fechaCheckinReal,
            LocalDateTime fechaCheckoutReal, List<HabitacionReservadaDTO> habitaciones) {
        this.idReserva = idReserva;
        this.codigoReserva = codigoReserva;
        this.nombreCliente = nombreCliente;
        this.emailCliente = emailCliente;
        this.fechaReserva = fechaReserva;
        this.fechaCheckin = fechaCheckin;
        this.fechaCheckout = fechaCheckout;
        this.numHuespedes = numHuespedes;
        this.total = total;
        this.estado = estado;
        this.fechaCheckinReal = fechaCheckinReal;
        this.fechaCheckoutReal = fechaCheckoutReal;
        this.habitaciones = habitaciones;
    }

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public void setCodigoReserva(String codigoReserva) {
        this.codigoReserva = codigoReserva;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDate getFechaCheckin() {
        return fechaCheckin;
    }

    public void setFechaCheckin(LocalDate fechaCheckin) {
        this.fechaCheckin = fechaCheckin;
    }

    public LocalDate getFechaCheckout() {
        return fechaCheckout;
    }

    public void setFechaCheckout(LocalDate fechaCheckout) {
        this.fechaCheckout = fechaCheckout;
    }

    public Integer getNumHuespedes() {
        return numHuespedes;
    }

    public void setNumHuespedes(Integer numHuespedes) {
        this.numHuespedes = numHuespedes;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCheckinReal() {
        return fechaCheckinReal;
    }

    public void setFechaCheckinReal(LocalDateTime fechaCheckinReal) {
        this.fechaCheckinReal = fechaCheckinReal;
    }

    public LocalDateTime getFechaCheckoutReal() {
        return fechaCheckoutReal;
    }

    public void setFechaCheckoutReal(LocalDateTime fechaCheckoutReal) {
        this.fechaCheckoutReal = fechaCheckoutReal;
    }

    public List<HabitacionReservadaDTO> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<HabitacionReservadaDTO> habitaciones) {
        this.habitaciones = habitaciones;
    }

    public String getEstadoTexto() {
        if (estado == null) {
            return "";
        }
        return switch (estado) {
            case pendiente -> "Pendiente";
            case confirmada -> "Confirmada";
            case cancelada -> "Cancelada";
            case completada -> "Completada";
        };
    }
}
