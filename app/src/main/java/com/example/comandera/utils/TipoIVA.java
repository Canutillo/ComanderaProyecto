package com.example.comandera.utils;

public class TipoIVA {
    private int id;
    private double valorIVA;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValorIVA() {
        return valorIVA;
    }

    public void setValorIVA(double valorIVA) {
        this.valorIVA = valorIVA;
    }

    @Override
    public String toString() {
        return id + " || " + valorIVA + "\n";
    }

}
