package com.libreria.dto;

import java.math.BigDecimal;

public class VentasPorMesDTO {
    private Integer mes;
    private BigDecimal total;

    public VentasPorMesDTO() {
    }

    public VentasPorMesDTO(Integer mes, BigDecimal total) {
        this.mes = mes;
        this.total = total;
    }

    // Getters y Setters
    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
