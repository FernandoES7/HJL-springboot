package com.core.hostal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.core.hostal.enums.EstadoReserva;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "reserva",
    uniqueConstraints = @UniqueConstraint(name = "uq_reserva_codigo", columnNames = "codigo_reserva")
)
public class Reserva extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;

    @Column(name = "codigo_reserva", nullable = false, length = 20)
    private String codigoReserva;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDate fechaReserva;

    @Column(name = "fecha_checkin", nullable = false)
    private LocalDate fechaCheckin;

    @Column(name = "fecha_checkout", nullable = false)
    private LocalDate fechaCheckout;

    @Column(name = "num_huespedes", nullable = false)
    private Integer numHuespedes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('pendiente', 'confirmada', 'cancelada', 'completada')")
    private EstadoReserva estado = EstadoReserva.pendiente;

    @Column(name = "fecha_checkin_real")
    private LocalDateTime fechaCheckinReal;

    @Column(name = "observacion_checkin", length = 255)
    private String observacionCheckin;

    @Column(name = "fecha_checkout_real")
    private LocalDateTime fechaCheckoutReal;

    @Column(name = "observacion_checkout", length = 255)
    private String observacionCheckout;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaHabitacion> habitaciones = new ArrayList<>();

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cancelacion cancelacion;

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private Factura factura;

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public void setCodigoReserva(String codigoReserva) {
        this.codigoReserva = codigoReserva;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDate getFechaCheckin() {
        return fechaCheckin;
    }

    public void setFechaCheckin(LocalDate fechaCheckin) {
        this.fechaCheckin = fechaCheckin;
    }

    public LocalDate getFechaCheckout() {
        return fechaCheckout;
    }

    public void setFechaCheckout(LocalDate fechaCheckout) {
        this.fechaCheckout = fechaCheckout;
    }

    public Integer getNumHuespedes() {
        return numHuespedes;
    }

    public void setNumHuespedes(Integer numHuespedes) {
        this.numHuespedes = numHuespedes;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCheckinReal() {
        return fechaCheckinReal;
    }

    public void setFechaCheckinReal(LocalDateTime fechaCheckinReal) {
        this.fechaCheckinReal = fechaCheckinReal;
    }

    public String getObservacionCheckin() {
        return observacionCheckin;
    }

    public void setObservacionCheckin(String observacionCheckin) {
        this.observacionCheckin = observacionCheckin;
    }

    public LocalDateTime getFechaCheckoutReal() {
        return fechaCheckoutReal;
    }

    public void setFechaCheckoutReal(LocalDateTime fechaCheckoutReal) {
        this.fechaCheckoutReal = fechaCheckoutReal;
    }

    public String getObservacionCheckout() {
        return observacionCheckout;
    }

    public void setObservacionCheckout(String observacionCheckout) {
        this.observacionCheckout = observacionCheckout;
    }

    public List<ReservaHabitacion> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<ReservaHabitacion> habitaciones) {
        this.habitaciones = habitaciones;
    }

    public void addHabitacion(ReservaHabitacion detalle) {
        habitaciones.add(detalle);
        detalle.setReserva(this);
    }

    public Cancelacion getCancelacion() {
        return cancelacion;
    }

    public void setCancelacion(Cancelacion cancelacion) {
        this.cancelacion = cancelacion;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }
}
