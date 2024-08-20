package com.example.comandera.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Ticket implements Parcelable {
    private int id;
    private int estadoDocumento;
    private String fecha;
    private double numero;
    private double serieId;

    // Constructor
    public Ticket() {
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
