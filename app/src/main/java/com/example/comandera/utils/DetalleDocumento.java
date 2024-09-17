package com.example.comandera.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DetalleDocumento {
    private int articuloID;
    private String descripcion;
    private String descripcionLarga;
    private int cantidad;
    private BigDecimal totalLinea;
    private BigDecimal pvp;

    public DetalleDocumento() {
    }

    public DetalleDocumento(String descripcionLarga, int cantidad, double pvp, double totalLinea) {
        this.descripcionLarga = descripcionLarga;
        this.cantidad = cantidad;
        this.pvp = BigDecimal.valueOf(pvp).setScale(2, RoundingMode.HALF_UP);
        this.totalLinea = BigDecimal.valueOf(totalLinea).setScale(2, RoundingMode.HALF_UP);
    }

    public DetalleDocumento(int articuloID, String descripcionLarga, int cantidad, double pvp, double totalLinea) {
        this.articuloID=articuloID;
        this.descripcionLarga = descripcionLarga;
        this.cantidad = cantidad;
        this.pvp = BigDecimal.valueOf(pvp).setScale(2, RoundingMode.HALF_UP);
        this.totalLinea = BigDecimal.valueOf(totalLinea).setScale(2, RoundingMode.HALF_UP);
    }


    public int getArticuloID() {
        return articuloID;
    }

    public void setArticuloID(int articuloID) {
        this.articuloID = articuloID;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcionLarga() {
        return descripcionLarga;
    }

    public void setDescripcionLarga(String descripcionLarga) {
        this.descripcionLarga = descripcionLarga;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotalLinea() {
        return totalLinea;
    }


    public void setTotalLinea(double totalLinea) {
        this.totalLinea = BigDecimal.valueOf(totalLinea).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPvp() {
        return pvp;
    }

    public void setPvp(double pvp) {
        this.pvp = BigDecimal.valueOf(pvp).setScale(2, RoundingMode.HALF_UP);
    }


    @Override
    public String toString() {
        return articuloID + " || " +
                descripcionLarga + " || " +
                cantidad + " || " +
                totalLinea + " || " +
                pvp;
    }
}
