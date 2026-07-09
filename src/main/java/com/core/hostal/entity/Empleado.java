package com.core.hostal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.core.hostal.enums.EstadoEmpleado;
import com.core.hostal.enums.RolEmpleado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleado")
public class Empleado extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer idEmpleado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hotel", nullable = false)
    private Hotel hotel;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('admin', 'gerente', 'recepcion', 'limpieza')")
    private RolEmpleado rol = RolEmpleado.admin;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('activo', 'inactivo')")
    private EstadoEmpleado estado = EstadoEmpleado.activo;

    @Column(name = "reset_token", length = 255)
    private String resetToken;

    @Column(name = "reset_token_expires")
    private LocalDateTime resetTokenExpires;

    @OneToMany(mappedBy = "empleado")
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "empleado")
    private List<Cancelacion> cancelaciones = new ArrayList<>();

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public RolEmpleado getRol() {
        return rol;
    }

    public void setRol(RolEmpleado rol) {
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public EstadoEmpleado getEstado() {
        return estado;
    }

    public void setEstado(EstadoEmpleado estado) {
        this.estado = estado;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpires() {
        return resetTokenExpires;
    }

    public void setResetTokenExpires(LocalDateTime resetTokenExpires) {
        this.resetTokenExpires = resetTokenExpires;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public List<Cancelacion> getCancelaciones() {
        return cancelaciones;
    }

    public void setCancelaciones(List<Cancelacion> cancelaciones) {
        this.cancelaciones = cancelaciones;
    }
}
