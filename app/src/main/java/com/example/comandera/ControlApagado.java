package com.example.comandera;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.Ticket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ControlApagado extends Service {

    private VariablesGlobales varGlob;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.varGlob = (VariablesGlobales) getApplication();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
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
                }
            }
        });
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS); // Espera hasta 10 segundos que el hilo termine
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopSelf();

    }
}