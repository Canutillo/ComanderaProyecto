package com.example.comandera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.Ticket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ControlBloqueo extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
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
                    //Cerrar la app para evitar errores si el onResumed no funcionara
/*                    Intent intent = new Intent(context, CerrarAplicacionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);*/
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