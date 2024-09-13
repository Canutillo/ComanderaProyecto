package com.example.comandera.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FichaPersonal implements Parcelable {
    private int id;
    private String usuarioApp;
    private String contrasenaApp;
    private int seccionId1;
    private int seccionId2;
    private int seccionId3;
    private boolean accesoTpv;
    private int estado;

    @Override
    public String toString(){
        return this.usuarioApp;
    }

    public FichaPersonal(int id, String usuarioApp, String contrasenaApp, int seccionId1, int seccionId2, int seccionId3, boolean accesoTpv, int estado) {
        this.id = id;
        this.usuarioApp = usuarioApp;
        this.contrasenaApp = contrasenaApp;
        this.seccionId1 = seccionId1;
        this.seccionId2 = seccionId2;
        this.seccionId3 = seccionId3;
        this.accesoTpv = accesoTpv;
        this.estado = estado;
    }

    protected FichaPersonal(Parcel in) {
        id = in.readInt();
        usuarioApp = in.readString();
        contrasenaApp = in.readString();
        seccionId1 = in.readInt();
        seccionId2 = in.readInt();
        seccionId3 = in.readInt();
        accesoTpv = in.readByte() != 0;
        estado = in.readInt();
    }

    public static final Creator<FichaPersonal> CREATOR = new Creator<FichaPersonal>() {
        @Override
        public FichaPersonal createFromParcel(Parcel in) {
            return new FichaPersonal(in);
        }

        @Override
        public FichaPersonal[] newArray(int size) {
            return new FichaPersonal[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(usuarioApp);
        dest.writeString(contrasenaApp);
        dest.writeInt(seccionId1);
        dest.writeInt(seccionId2);
        dest.writeInt(seccionId3);
        dest.writeByte((byte) (accesoTpv ? 1 : 0));
        dest.writeInt(estado);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuarioApp() {
        return usuarioApp;
    }

    public void setUsuarioApp(String usuarioApp) {
        this.usuarioApp = usuarioApp;
    }

    public String getContrasenaApp() {
        return contrasenaApp;
    }

    public void setContrasenaApp(String contrasenaApp) {
        this.contrasenaApp = contrasenaApp;
    }

    public int getSeccionId1() {
        return seccionId1;
    }

    public void setSeccionId1(int seccionId1) {
        this.seccionId1 = seccionId1;
    }

    public int getSeccionId2() {
        return seccionId2;
    }

    public void setSeccionId2(int seccionId2) {
        this.seccionId2 = seccionId2;
    }

    public int getSeccionId3() {
        return seccionId3;
    }

    public void setSeccionId3(int seccionId3) {
        this.seccionId3 = seccionId3;
    }

    public boolean isAccesoTpv() {
        return accesoTpv;
    }

    public void setAccesoTpv(boolean accesoTpv) {
        this.accesoTpv = accesoTpv;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
