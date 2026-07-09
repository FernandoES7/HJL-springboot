package com.core.hostal.entity;

import java.util.ArrayList;
import java.util.List;

import com.core.hostal.enums.EstadoHabitacion;

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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "habitacion",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_habitacion_hotel_numero",
        columnNames = {"id_hotel", "numero"}
    )
)
public class Habitacion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoHabitacion tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hotel", nullable = false)
    private Hotel hotel;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(nullable = false)
    private Integer piso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('disponible', 'mantenimiento', 'ocupada')")
    private EstadoHabitacion estado = EstadoHabitacion.disponible;

    @OneToMany(mappedBy = "habitacion")
    private List<ReservaHabitacion> reservaHabitaciones = new ArrayList<>();

    public Integer getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public TipoHabitacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
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

    public List<ReservaHabitacion> getReservaHabitaciones() {
        return reservaHabitaciones;
    }

    public void setReservaHabitaciones(List<ReservaHabitacion> reservaHabitaciones) {
        this.reservaHabitaciones = reservaHabitaciones;
    }
}
