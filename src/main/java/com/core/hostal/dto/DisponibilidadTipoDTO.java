package com.core.hostal.dto;

import java.math.BigDecimal;

public class DisponibilidadTipoDTO {

    private Integer idTipo;
    private String nombreTipo;
    private String descripcion;
    private Integer capacidad;
    private BigDecimal precioBase;
    private String imagenUrl;
    private Long habitacionesDisponibles;

    public DisponibilidadTipoDTO() {
    }

    public DisponibilidadTipoDTO(Integer idTipo, String nombreTipo, String descripcion,
            Integer capacidad, BigDecimal precioBase, String imagenUrl, Long habitacionesDisponibles) {
        this.idTipo = idTipo;
        this.nombreTipo = nombreTipo;
        this.descripcion = descripcion;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.imagenUrl = imagenUrl;
        this.habitacionesDisponibles = habitacionesDisponibles;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Long getHabitacionesDisponibles() {
        return habitacionesDisponibles;
    }

    public void setHabitacionesDisponibles(Long habitacionesDisponibles) {
        this.habitacionesDisponibles = habitacionesDisponibles;
    }
}
