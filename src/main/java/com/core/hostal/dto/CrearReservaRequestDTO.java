package com.core.hostal.dto;

import java.time.LocalDate;
import java.util.List;

public class CrearReservaRequestDTO {

    private LocalDate fechaCheckin;
    private LocalDate fechaCheckout;
    private Integer numHuespedes;
    private String nombre;
    private String email;
    private String documento;
    private String telefono;
    private String tarjetaReferencia;
    private List<HabitacionSeleccionRequestDTO> habitaciones;

    public CrearReservaRequestDTO() {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTarjetaReferencia() {
        return tarjetaReferencia;
    }

    public void setTarjetaReferencia(String tarjetaReferencia) {
        this.tarjetaReferencia = tarjetaReferencia;
    }

    public List<HabitacionSeleccionRequestDTO> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<HabitacionSeleccionRequestDTO> habitaciones) {
        this.habitaciones = habitaciones;
    }
}
