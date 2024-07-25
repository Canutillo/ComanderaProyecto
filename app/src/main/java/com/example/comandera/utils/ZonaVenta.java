package com.example.comandera.utils;

public class ZonaVenta {

    private int id;
    private String zona;

    public ZonaVenta(int id, String zona) {
        this.id = id;
        this.zona = zona;
    }

    public int getId() {
        return id;
    }

    public String getZona() {
        return zona;
    }

    @Override
    public String toString() {
        return "Zona{id=" + id + ", zona='" + zona + "'}";
    }
}
