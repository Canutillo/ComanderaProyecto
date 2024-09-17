package com.example.comandera.utils;

public class Articulo {
    private int id;
    private String codigo;
    private int familia_id;
    private String nombre;
    private String url;
    private int tipoIVAid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getFamilia_id() {
        return familia_id;
    }

    public void setFamilia_id(int familia_id) {
        this.familia_id = familia_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTipoIVAid() {
        return tipoIVAid;
    }

    public void setTipoIVAid(int tipoIVAid) {
        this.tipoIVAid = tipoIVAid;
    }

    @Override
    public String toString() {
        return id + " || " +
                codigo + " || " +
                familia_id + " || " +
                nombre + " || " +
                url+" || "+
                tipoIVAid;
    }
}
