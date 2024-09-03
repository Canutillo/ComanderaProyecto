package com.example.comandera.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DetalleDocumento {
    private String descripcionLarga;
    private int cantidad;
    private BigDecimal totalLinea;
    private BigDecimal pvp;

    public DetalleDocumento(String descripcionLarga, int cantidad, double pvp, double totalLinea) {
        this.descripcionLarga = descripcionLarga;
        this.cantidad = cantidad;
        this.pvp = BigDecimal.valueOf(pvp).setScale(2, RoundingMode.HALF_UP);
        this.totalLinea = BigDecimal.valueOf(totalLinea).setScale(2, RoundingMode.HALF_UP);
    }

    // Getters
    public String getDescripcionLarga() {
        return descripcionLarga;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPvp() {
        return pvp;
    }

    public String getPvpFormatted() {
        return pvp.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public void setPvp(double pvp) {
        this.pvp = BigDecimal.valueOf(pvp).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalLinea() {
        return totalLinea;
    }

    public String getTotalLineaFormatted() {
        return totalLinea.setScale(2, RoundingMode.HALF_UP).toString();
    }
}
