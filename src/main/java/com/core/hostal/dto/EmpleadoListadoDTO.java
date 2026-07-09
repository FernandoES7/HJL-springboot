package com.core.hostal.dto;

import com.core.hostal.enums.EstadoEmpleado;
import com.core.hostal.enums.RolEmpleado;

public class EmpleadoListadoDTO {

    private Integer idEmpleado;
    private String nombre;
    private String email;
    private String telefono;
    private RolEmpleado rol;
    private EstadoEmpleado estado;
    private String nombreHotel;

    public EmpleadoListadoDTO() {
    }

    public EmpleadoListadoDTO(Integer idEmpleado, String nombre, String email, String telefono,
            RolEmpleado rol, EstadoEmpleado estado, String nombreHotel) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
        this.estado = estado;
        this.nombreHotel = nombreHotel;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public RolEmpleado getRol() {
        return rol;
    }

    public void setRol(RolEmpleado rol) {
        this.rol = rol;
    }

    public EstadoEmpleado getEstado() {
        return estado;
    }

    public void setEstado(EstadoEmpleado estado) {
        this.estado = estado;
    }

    public String getNombreHotel() {
        return nombreHotel;
    }

    public void setNombreHotel(String nombreHotel) {
        this.nombreHotel = nombreHotel;
    }
}
