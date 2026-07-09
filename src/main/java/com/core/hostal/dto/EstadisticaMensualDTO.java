package com.core.hostal.dto;

import java.math.BigDecimal;

public class EstadisticaMensualDTO {

    private int mesNum;
    private String mesNombre;
    private BigDecimal ingresos;
    private int numReservas;
    private int ocupacionPct;

    public EstadisticaMensualDTO() {
        this.ingresos = BigDecimal.ZERO;
    }

    public EstadisticaMensualDTO(int mesNum, String mesNombre, BigDecimal ingresos,
            int numReservas, int ocupacionPct) {
        this.mesNum = mesNum;
        this.mesNombre = mesNombre;
        this.ingresos = ingresos != null ? ingresos : BigDecimal.ZERO;
        this.numReservas = numReservas;
        this.ocupacionPct = ocupacionPct;
    }

    public int getMesNum() {
        return mesNum;
    }

    public void setMesNum(int mesNum) {
        this.mesNum = mesNum;
    }

    public String getMesNombre() {
        return mesNombre;
    }

    public void setMesNombre(String mesNombre) {
        this.mesNombre = mesNombre;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public int getNumReservas() {
        return numReservas;
    }

    public void setNumReservas(int numReservas) {
        this.numReservas = numReservas;
    }

    public int getOcupacionPct() {
        return ocupacionPct;
    }

    public void setOcupacionPct(int ocupacionPct) {
        this.ocupacionPct = ocupacionPct;
    }
}
