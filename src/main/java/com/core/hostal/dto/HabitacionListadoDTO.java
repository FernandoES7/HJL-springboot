package com.core.hostal.dto;

import com.core.hostal.enums.EstadoHabitacion;

public class HabitacionListadoDTO {

    private Integer idHabitacion;
    private String numero;
    private Integer piso;
    private EstadoHabitacion estado;
    private String nombreTipo;
    private Integer capacidadTipo;
    private String imagenUrlTipo;

    public HabitacionListadoDTO() {
    }

    public HabitacionListadoDTO(Integer idHabitacion, String numero, Integer piso,
            EstadoHabitacion estado, String nombreTipo, Integer capacidadTipo, String imagenUrlTipo) {
        this.idHabitacion = idHabitacion;
        this.numero = numero;
        this.piso = piso;
        this.estado = estado;
        this.nombreTipo = nombreTipo;
        this.capacidadTipo = capacidadTipo;
        this.imagenUrlTipo = imagenUrlTipo;
    }

    public Integer getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public Integer getCapacidadTipo() {
        return capacidadTipo;
    }

    public void setCapacidadTipo(Integer capacidadTipo) {
        this.capacidadTipo = capacidadTipo;
    }

    public String getImagenUrlTipo() {
        return imagenUrlTipo;
    }

    public void setImagenUrlTipo(String imagenUrlTipo) {
        this.imagenUrlTipo = imagenUrlTipo;
    }
}
