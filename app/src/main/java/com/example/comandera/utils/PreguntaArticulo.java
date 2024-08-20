package com.example.comandera.utils;

public class PreguntaArticulo {
    private int id;
    private String texto;

    public PreguntaArticulo(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }
}