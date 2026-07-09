package com.core.hostal.dto;

import java.math.BigDecimal;

public class ResumenDashboardDTO {

    private int totalHabitaciones;
    private int habitacionesOcupadas;
    private int habitacionesDisponibles;
    private int reservasActivas;
    private BigDecimal ingresosAnio;
    private int totalClientes;
    private int totalReservasAnio;
    private int reservasConfirmadas;
    private int tasaOcupacion;

    public ResumenDashboardDTO() {
        this.ingresosAnio = BigDecimal.ZERO;
    }

    public int getTotalHabitaciones() {
        return totalHabitaciones;
    }

    public void setTotalHabitaciones(int totalHabitaciones) {
        this.totalHabitaciones = totalHabitaciones;
    }

    public int getHabitacionesOcupadas() {
        return habitacionesOcupadas;
    }

    public void setHabitacionesOcupadas(int habitacionesOcupadas) {
        this.habitacionesOcupadas = habitacionesOcupadas;
    }

    public int getHabitacionesDisponibles() {
        return habitacionesDisponibles;
    }

    public void setHabitacionesDisponibles(int habitacionesDisponibles) {
        this.habitacionesDisponibles = habitacionesDisponibles;
    }

    public int getReservasActivas() {
        return reservasActivas;
    }

    public void setReservasActivas(int reservasActivas) {
        this.reservasActivas = reservasActivas;
    }

    public BigDecimal getIngresosAnio() {
        return ingresosAnio;
    }

    public void setIngresosAnio(BigDecimal ingresosAnio) {
        this.ingresosAnio = ingresosAnio;
    }

    public int getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(int totalClientes) {
        this.totalClientes = totalClientes;
    }

    public int getTotalReservasAnio() {
        return totalReservasAnio;
    }

    public void setTotalReservasAnio(int totalReservasAnio) {
        this.totalReservasAnio = totalReservasAnio;
    }

    public int getReservasConfirmadas() {
        return reservasConfirmadas;
    }

    public void setReservasConfirmadas(int reservasConfirmadas) {
        this.reservasConfirmadas = reservasConfirmadas;
    }

    public int getTasaOcupacion() {
        return tasaOcupacion;
    }

    public void setTasaOcupacion(int tasaOcupacion) {
        this.tasaOcupacion = tasaOcupacion;
    }
}
