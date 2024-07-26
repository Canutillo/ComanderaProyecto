package com.example.comandera.utils;

public class Mesa {
    int id;
    int numero;
    String nombre;
    int zonaId;

    public int getZonaId() {
        return zonaId;
    }

    public void setZonaId(int zonaId) {
        this.zonaId = zonaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Mesa(int id, int numero, String nombre, int zonaId) {
        this.id = id;
        this.numero = numero;
        this.nombre = nombre;
        this.zonaId = zonaId;
    }
}
