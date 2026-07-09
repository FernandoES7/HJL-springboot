package com.core.hostal.dto;

public class HabitacionPorTipoDTO {

    private String nombreTipo;
    private int cantidad;

    public HabitacionPorTipoDTO() {
    }

    public HabitacionPorTipoDTO(String nombreTipo, int cantidad) {
        this.nombreTipo = nombreTipo;
        this.cantidad = cantidad;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
