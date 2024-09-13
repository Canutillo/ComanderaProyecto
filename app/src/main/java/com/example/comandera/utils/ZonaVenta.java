package com.example.comandera.utils;

import java.util.List;

public class ZonaVenta {

    private int id;
    private String zona;
    private List<Mesa> listaMesas;

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

    public List<Mesa> getListaMesas() {
        return listaMesas;
    }



    public void setListaMesas(List<Mesa> listaMesas) {
        this.listaMesas = listaMesas;
    }

    @Override
    public String toString() {
        return this.getId()+""/*"Zona{id=" + id + ", zona='" + zona + "'}"*/;
    }
}
