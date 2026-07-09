package com.core.hostal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.core.hostal.enums.EstadoReserva;

public class ReservaResumenDTO {

    private Integer idReserva;
    private String codigoReserva;
    private String nombreCliente;
    private LocalDate fechaCheckin;
    private LocalDate fechaCheckout;
    private Integer numHuespedes;
    private BigDecimal total;
    private EstadoReserva estado;

    public ReservaResumenDTO() {
    }

    public ReservaResumenDTO(Integer idReserva, String codigoReserva, String nombreCliente,
            LocalDate fechaCheckin, LocalDate fechaCheckout, Integer numHuespedes,
            BigDecimal total, EstadoReserva estado) {
        this.idReserva = idReserva;
        this.codigoReserva = codigoReserva;
        this.nombreCliente = nombreCliente;
        this.fechaCheckin = fechaCheckin;
        this.fechaCheckout = fechaCheckout;
        this.numHuespedes = numHuespedes;
        this.total = total;
        this.estado = estado;
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
