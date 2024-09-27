package com.example.comandera;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.Ticket;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Operaciones de base de datos
                System.out.println("A eliminar");
                if (varGlob.getTicketActual() != null) {
                    Ticket ticketActual=varGlob.getTicketActual();
                    TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
                    ticketBD.actualizaEscribiendo(false, ticketActual.getId());
                    ticketBD.borrarDetalles(ticketActual.getId());
                    ticketBD.actualizarTicket(ticketActual.getDetallesTicket(),ticketActual.getId());
                }
            }
        }).start();

        // Detener el servicio después de completar la tarea
        stopSelf();
    }
}