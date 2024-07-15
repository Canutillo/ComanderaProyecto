package com.example.comandera.utils;

public class User {
    private int Id;        // Cambiado a "Id" para coincidir con el JSON
    private String DNI;    // Cambiado a "DNI" para coincidir con el JSON
    private String Trabajador; // Cambiado a "Trabajador" para coincidir con el JSON
    private int Codigo;    // Cambiado a "Codigo" para coincidir con el JSON

    // Getters y setters
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String dni) {
        this.DNI = dni;
    }

    public String getTrabajador() {
        return Trabajador;
    }

    public void setTrabajador(String trabajador) {
        this.Trabajador = trabajador;
    }

    public int getCodigo() {
        return Codigo;
    }

    public void setCodigo(int codigo) {
        this.Codigo = codigo;
    }

    @Override
    public String toString() {
        return "User{" +
                "Id=" + Id +
                ", DNI='" + DNI + '\'' +
                ", Trabajador='" + Trabajador + '\'' +
                ", Codigo=" + Codigo +
                '}';
    }
}
