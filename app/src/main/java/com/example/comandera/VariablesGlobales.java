package com.example.comandera;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;

import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.utils.TarifasDeVenta;
import com.example.comandera.utils.TipoIVA;
import com.example.comandera.utils.Articulo;
import com.example.comandera.utils.Familia;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.Mesa;
import com.example.comandera.utils.Ticket;
import com.example.comandera.utils.ZonaVenta;

import java.sql.SQLException;
import java.util.List;

public class VariablesGlobales extends Application implements LifecycleObserver {

    private SQLServerConnection conexionSQL;
    private int seccionIdUsuariosActual;
    private int IdDispositivoActual;
    private String macActual;
    private List<FichaPersonal> listaUsuarios;
    private FichaPersonal usuarioActual;
    private List<ZonaVenta> listaZonas;
    private ZonaVenta zonaActual;
    private Mesa mesaActual;
    private Ticket ticketActual;
    private Familia familiaActual;
    private Articulo articuloActual;
    private List<TipoIVA> tiposIVA;
    private List<TarifasDeVenta> tarifasDeVentas;
    private int ordenPreparacionActual;
    private String nombreActivity;
    //Este boolean sirve para que cuando le doy al boton del centro del movil o al de aplicaciones recientes me guarde los datos y desocupe el ticket por lo que para controlarlo
    //tengo que cambiarlo a falso cada vez que haga algo voluntario en la app como ir a otra pagina usando la interfaz para que asi en el onUserLeaveHint este guarde y vaya a mesasActivity
    private boolean guardarYsalirFamiliasArticulos;
    private boolean nuevaEntrada;





    @Override
    public void onCreate() {
        super.onCreate();
        // Registra el observer del ciclo de vida
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                setNombreActivity(activity.getLocalClassName());
                if (conexionSQL == null && !nombreActivity.equals("MainActivity")) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent); //
                    System.out.println("La conexion era nula se va a recargar la aplicacion para que no falle");
                }else{
                    if (conexionSQL!=null){
                        try {
                            System.out.println("No es nula pero no detecta que este cerrada");
                            if(conexionSQL.getConexion().isClosed()){
                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(intent); //
                                System.out.println("La conexion estaba cerrada se va a recargar la aplicacion para que no falle");
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                Log.d("TAG", "|||"+nombreActivity.toUpperCase()+"|||");
            }



            @Override
            public void onActivityPaused(@NonNull Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d("TAG", "|||"+nombreActivity.toUpperCase()+"|||"+"PARADO");
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
            }

        });
    }




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

    public Ticket getTicketActual() {
        return ticketActual;
    }

    public void setTicketActual(Ticket ticketActual) {
        this.ticketActual = ticketActual;
    }

    public Familia getFamiliaActual() {
        return familiaActual;
    }

    public void setFamiliaActual(Familia familiaActual) {
        this.familiaActual = familiaActual;
    }

    public Articulo getArticuloActual() {
        return articuloActual;
    }

    public void setArticuloActual(Articulo articuloActual) {
        this.articuloActual = articuloActual;
    }

    public List<TipoIVA> getTiposIVA() {
        return tiposIVA;
    }

    public void setTiposIVA(List<TipoIVA> tiposIVA) {
        this.tiposIVA = tiposIVA;
    }

    public List<TarifasDeVenta> getTarifasDeVentas() {
        return tarifasDeVentas;
    }

    public void setTarifasDeVentas(List<TarifasDeVenta> tarifasDeVentas) {
        this.tarifasDeVentas = tarifasDeVentas;
    }

    public int getOrdenPreparacionActual() {
        return ordenPreparacionActual;
    }

    public void setOrdenPreparacionActual(int ordenPreparacionActual) {
        this.ordenPreparacionActual = ordenPreparacionActual;
    }

    public String getNombreActivity() {
        return nombreActivity;
    }

    public void setNombreActivity(String nombreActivity) {
        this.nombreActivity = nombreActivity;
    }

    public boolean isGuardarYsalirFamiliasArticulos() {
        return guardarYsalirFamiliasArticulos;
    }

    public void setGuardarYsalirFamiliasArticulos(boolean guardarYsalirFamiliasArticulos) {
        this.guardarYsalirFamiliasArticulos = guardarYsalirFamiliasArticulos;
    }

    public boolean isNuevaEntrada() {
        return nuevaEntrada;
    }

    public void setNuevaEntrada(boolean nuevaEntrada) {
        this.nuevaEntrada = nuevaEntrada;
    }
}
