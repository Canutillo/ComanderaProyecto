package com.example.comandera;

import android.app.Application;

import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.Mesa;
import com.example.comandera.utils.ZonaVenta;

import java.util.List;

public class VariablesGlobales extends Application {
    private SQLServerConnection conexionSQL;
    private int seccionIdUsuariosActual;
    private int IdDispositivoActual;
    private String macActual;
    private List<FichaPersonal> listaUsuarios;
    private FichaPersonal usuarioActual;
    private List<ZonaVenta> listaZonas;
    private ZonaVenta zonaActual;
    private Mesa mesaActual;

    public SQLServerConnection getConexionSQL() {
        return conexionSQL;
    }

    public void setConexionSQL(SQLServerConnection conexionSQL) {
        this.conexionSQL = conexionSQL;
    }

    public int getSeccionIdUsuariosActual() {
        return seccionIdUsuariosActual;
    }

    public void setSeccionIdUsuariosActual(int seccionIdUsuariosActual) {
        this.seccionIdUsuariosActual = seccionIdUsuariosActual;
    }

    public int getIdDispositivoActual() {
        return IdDispositivoActual;
    }

    public void setIdDispositivoActual(int idDispositivoActual) {
        IdDispositivoActual = idDispositivoActual;
    }

    public String getMacActual() {
        return macActual;
    }

    public void setMacActual(String macActual) {
        this.macActual = macActual;
    }

    public List<FichaPersonal> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<FichaPersonal> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public FichaPersonal getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(FichaPersonal usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public List<ZonaVenta> getListaZonas() {
        return listaZonas;
    }

    public void setListaZonas(List<ZonaVenta> listaZonas) {
        this.listaZonas = listaZonas;
    }

    public ZonaVenta getZonaActual() {
        return zonaActual;
    }

    public void setZonaActual(ZonaVenta zonaActual) {
        this.zonaActual = zonaActual;
    }

    public Mesa getMesaActual() {
        return mesaActual;
    }

    public void setMesaActual(Mesa mesaActual) {
        this.mesaActual = mesaActual;
    }
}
