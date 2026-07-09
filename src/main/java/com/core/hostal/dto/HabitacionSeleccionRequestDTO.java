package com.core.hostal.dto;

public class HabitacionSeleccionRequestDTO {

    private Integer idTipo;
    private Integer cantidad;

    public HabitacionSeleccionRequestDTO() {
    }

    public HabitacionSeleccionRequestDTO(Integer idTipo, Integer cantidad) {
        this.idTipo = idTipo;
        this.cantidad = cantidad;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
