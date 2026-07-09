package com.core.hostal.dto;

import java.math.BigDecimal;

public class ReservaCreadaResponseDTO {

    private Integer idReserva;
    private String codigoReserva;
    private BigDecimal total;

    public ReservaCreadaResponseDTO() {
    }

    public ReservaCreadaResponseDTO(Integer idReserva, String codigoReserva, BigDecimal total) {
        this.idReserva = idReserva;
        this.codigoReserva = codigoReserva;
        this.total = total;
    }

    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public void setCodigoReserva(String codigoReserva) {
        this.codigoReserva = codigoReserva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
