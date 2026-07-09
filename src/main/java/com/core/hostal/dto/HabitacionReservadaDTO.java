package com.core.hostal.dto;

import java.math.BigDecimal;

public class HabitacionReservadaDTO {

    private Integer idHabitacion;
    private String numero;
    private Integer piso;
    private String nombreTipo;
    private BigDecimal precioNoche;

    public HabitacionReservadaDTO() {
    }

    public HabitacionReservadaDTO(Integer idHabitacion, String numero, Integer piso,
            String nombreTipo, BigDecimal precioNoche) {
        this.idHabitacion = idHabitacion;
        this.numero = numero;
        this.piso = piso;
        this.nombreTipo = nombreTipo;
        this.precioNoche = precioNoche;
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

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public BigDecimal getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(BigDecimal precioNoche) {
        this.precioNoche = precioNoche;
    }
}
