package com.example.comandera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.Ticket;

import java.sql.SQLOutput;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ControlBloqueo extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "BroadcastCreado ");
        VariablesGlobales varGlob=(VariablesGlobales) context.getApplicationContext();
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            // La pantalla se ha bloqueado
            // Crear un nuevo hilo para realizar la operación en segundo plano
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // Operaciones de base de datos
                    if (varGlob.getTicketActual() != null) {
                        Ticket ticketActual = varGlob.getTicketActual();
                        TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
                        ticketBD.actualizaEscribiendo(false, ticketActual.getId());
                        ticketBD.borrarDetalles(ticketActual.getId());
                        ticketBD.actualizarTicket(ticketActual.getDetallesTicket(), ticketActual.getId());
                        System.out.println("Bloqueado TASKS");
                    }
                    //Dependiendo de donde este en ese momento la app nos vamos a un lado o a otro
                    String nombreActivity= varGlob.getNombreActivity();
                    if(nombreActivity.equals("MainActivity")||nombreActivity.equals("ConfigActivity")||nombreActivity.equals("UsuariosActivity")||nombreActivity.equals("ContrasenaActivity")){
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        context.unregisterReceiver(ControlBloqueo.this);
                    }else{
                        Intent intent = new Intent(context, MesasActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    System.out.println("Bloqueado");
                }
            });
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS); // Espera hasta 10 segundos que el hilo termine
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}