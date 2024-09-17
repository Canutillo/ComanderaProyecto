package com.example.comandera.utils;

import java.util.List;

public class ZonaVenta {


    private int id;
    private String zona;
    private int idTarifaVenta;
    private List<Mesa> listaMesas;

    public ZonaVenta(int id, String zona, int idTarifa) {
        this.id = id;
        this.zona = zona;
        this.idTarifaVenta=idTarifa;
    }

    public int getId() {
        return id;
    }

    public String getZona() {
        return zona;
    }

    public int getIdTarifaVenta() {
        return idTarifaVenta;
    }

    public void setIdTarifaVenta(int idTarifaVenta) {
        this.idTarifaVenta = idTarifaVenta;
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
