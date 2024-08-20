package com.example.comandera.utils;

public class Dispositivo {
    int id;
    String descripcion;
    int seccionId;

    public Dispositivo(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(int seccionId) {
        this.seccionId = seccionId;
    }

    @Override
    public String toString() {
        return descripcion; // Solo la descripción se mostrará en el Spinner
    }

}
