package com.core.hostal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.core.hostal.enums.EstadoFactura;

public class FacturaResumenDTO {

    private Integer idFactura;
    private String numeroFactura;
    private String codigoReserva;
    private String nombreCliente;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private LocalDate fecha;
    private EstadoFactura estado;

    public FacturaResumenDTO() {
    }

    public FacturaResumenDTO(Integer idFactura, String numeroFactura, String codigoReserva,
            String nombreCliente, BigDecimal subtotal, BigDecimal impuestos, BigDecimal total,
            LocalDate fecha, EstadoFactura estado) {
        this.idFactura = idFactura;
        this.numeroFactura = numeroFactura;
        this.codigoReserva = codigoReserva;
        this.nombreCliente = nombreCliente;
        this.subtotal = subtotal;
        this.impuestos = impuestos;
        this.total = total;
        this.fecha = fecha;
        this.estado = estado;
    }

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public void setCodigoReserva(String codigoReserva) {
        this.codigoReserva = codigoReserva;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }
}
