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
    private double precio;
    private double iva;
    private double cuotaIva;
    private int ordenPreparacion;
    private int estadoComanda;
    private boolean otraRonda;

    public DetalleDocumento() {
    }

    public DetalleDocumento(DetalleDocumento detalle) {
        this.articuloID = detalle.articuloID;
        this.descripcion = detalle.descripcion;
        this.descripcionLarga = detalle.descripcionLarga;
        this.cantidad = detalle.cantidad;
        this.totalLinea = detalle.totalLinea;
        this.pvp = detalle.pvp;
        this.precio = detalle.precio;
        this.iva = detalle.iva;
        this.cuotaIva = detalle.cuotaIva;
        this.ordenPreparacion = detalle.ordenPreparacion;
        this.estadoComanda = detalle.estadoComanda;
        this.otraRonda = detalle.otraRonda;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getCuotaIva() {
        return cuotaIva;
    }

    public void setCuotaIva(double cuotaIva) {
        this.cuotaIva = cuotaIva;
    }

    public int getOrdenPreparacion() {
        return ordenPreparacion;
    }

    public void setOrdenPreparacion(int ordenPreparacion) {
        this.ordenPreparacion = ordenPreparacion;
    }

    public int getEstadoComanda() {
        return estadoComanda;
    }

    public void setEstadoComanda(int estadoComanda) {
        this.estadoComanda = estadoComanda;
    }

    public boolean isOtraRonda() {
        return otraRonda;
    }

    public void setOtraRonda(boolean otraRonda) {
        this.otraRonda = otraRonda;
    }

    @Override
    public String toString() {
        return "<|||"+estadoComanda+"|||>\n";
    }
}
