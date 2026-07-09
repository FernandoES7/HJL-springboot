package com.core.hostal.dto;

import java.math.BigDecimal;

public class TipoHabitacionAdminDTO {

    private Integer idTipo;
    private String nombre;
    private String descripcion;
    private Integer capacidad;
    private BigDecimal precioBase;
    private String imagenUrl;
    private Integer cantidadTotal;
    private Boolean activo;
    private Long totalHabitaciones;

    public TipoHabitacionAdminDTO() {
    }

    public TipoHabitacionAdminDTO(Integer idTipo, String nombre, String descripcion,
            Integer capacidad, BigDecimal precioBase, String imagenUrl, Integer cantidadTotal,
            Boolean activo, Long totalHabitaciones) {
        this.idTipo = idTipo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.imagenUrl = imagenUrl;
        this.cantidadTotal = cantidadTotal;
        this.activo = activo;
        this.totalHabitaciones = totalHabitaciones;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public Integer getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Long getTotalHabitaciones() {
        return totalHabitaciones;
    }

    public void setTotalHabitaciones(Long totalHabitaciones) {
        this.totalHabitaciones = totalHabitaciones;
    }
}
