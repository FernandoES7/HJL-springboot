package com.core.hostal.dto;

import java.time.LocalDate;

import com.core.hostal.enums.EstadoCliente;

public class ClienteListadoDTO {

    private Integer idCliente;
    private String nombre;
    private String email;
    private String telefono;
    private String documento;
    private LocalDate fechaRegistro;
    private EstadoCliente estado;

    public ClienteListadoDTO() {
    }

    public ClienteListadoDTO(Integer idCliente, String nombre, String email, String telefono,
            String documento, LocalDate fechaRegistro, EstadoCliente estado) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.documento = documento;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public EstadoCliente getEstado() {
        return estado;
    }

    public void setEstado(EstadoCliente estado) {
        this.estado = estado;
    }
}
