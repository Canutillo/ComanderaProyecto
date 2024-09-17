package com.example.comandera.utils;

public class TarifasDeVenta {
    private int id;
    private double precio;
    private int tipoTarifa;
    private int articuloId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getTipoTarifa() {
        return tipoTarifa;
    }

    public void setTipoTarifa(int tipoTarifa) {
        this.tipoTarifa = tipoTarifa;
    }

    public int getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(int articuloId) {
        this.articuloId = articuloId;
    }

    @Override
    public String toString() {
        return id + " || " + precio + " || " + tipoTarifa + " || " + articuloId +"\n";
    }

}
