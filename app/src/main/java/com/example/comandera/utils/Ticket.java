package com.example.comandera.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Ticket implements Parcelable {
    private boolean nuevo=false;
    private int id;
    private int comensales;
    private int estadoDocumento;
    private String fecha;
    private double numero;
    private double serieId;
    private List<DetalleDocumento> detallesTicket=new ArrayList<>();

    // Constructor
    public Ticket() {
    }

    // Getters and Setters


    public boolean isNuevo() {
        return nuevo;
    }

    public void setNuevo(boolean nuevo) {
        this.nuevo = nuevo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComensales() {
        return comensales;
    }

    public void setComensales(int comensales) {
        this.comensales = comensales;
    }

    public int getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(int estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getNumero() {
        return numero;
    }

    public void setNumero(double numero) {
        this.numero = numero;
    }

    public double getSerieId() {
        return serieId;
    }

    public void setSerieId(double serieId) {
        this.serieId = serieId;
    }

    public List<DetalleDocumento> getDetallesTicket() {
        return detallesTicket;
    }

    public void setDetallesTicket(List<DetalleDocumento> detalles) {
        this.detallesTicket = detalles;
    }


   /* public void anadirArticulo(Articulo articulo){
        DetalleDocumento detalle= new DetalleDocumento(articulo.getId(), articulo.getNombre(),1,)
    }*/

    @Override
    public String toString() {
        return nuevo+" || "+
                id + " || " +
                comensales + " || " +
                estadoDocumento + " || " +
                fecha + " || " +
                numero + " || " +
                serieId + " || " +
                (detallesTicket != null ? detallesTicket.toString() : "null");
    }
    //BORRAR DESDE AQUI
    // Parcelable implementation
    protected Ticket(Parcel in) {
        estadoDocumento = in.readInt();
        fecha = in.readString();
        numero = in.readDouble();
        serieId = in.readDouble();
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(estadoDocumento);
        parcel.writeString(fecha);
        parcel.writeDouble(numero);
        parcel.writeDouble(serieId);
    }
    //BORRAR HASTA AQUI
}
