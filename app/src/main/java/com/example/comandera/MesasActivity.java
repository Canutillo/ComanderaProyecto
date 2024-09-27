package com.example.comandera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.adapters.MesasAdapter;
import com.example.comandera.adapters.ZonasAdapter;
import com.example.comandera.data.TicketBD;
import com.example.comandera.data.UsuariosBD;
import com.example.comandera.utils.Ticket;
import com.example.comandera.utils.ZonaVenta;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MesasActivity extends AppCompatActivity {
    VariablesGlobales varGlob;
    TextView tvUser;
    RecyclerView recyclerViewZonas, recyclerViewMesas;
    ZonasAdapter zonasAdapter;
    MesasAdapter mesasAdapter;
    private Button botonCerrarSesion;
    private ScheduledExecutorService scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mesas);
        System.out.println("Mesas");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        varGlob=(VariablesGlobales) getApplicationContext();
        varGlob.setOrdenPreparacionActual(0);
        tvUser = findViewById(R.id.tvUser);
        recyclerViewZonas = findViewById(R.id.recyclerViewZonas);
        recyclerViewMesas = findViewById(R.id.recyclerViewMesas);

        //Boton Cerrar Sesion
        botonCerrarSesion=findViewById(R.id.botonCerrarSesion);
        botonCerrarSesion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MesasActivity.this);
                builder.setMessage("¿Quiere cerrar sesion?")
                        .setTitle("Cerrar Sesion");
                // 3. Add buttons
                builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Cierra sesion en la tabla dispositivos_usuarios
                        new CerrarSesionTask().execute();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Cancela y no pasa nada
                    }
                });
                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });

        recyclerViewZonas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMesas.setLayoutManager(new GridLayoutManager(this, 4));

        //Click en las mesas
        if(varGlob.getUsuarioActual() != null){
            tvUser.setText("Comandera/ " +varGlob.getUsuarioActual().getUsuarioApp());
            //Carga de las zonas en el adapter.
            if (!varGlob.getListaZonas().isEmpty()) {
                zonasAdapter = new ZonasAdapter(varGlob.getListaZonas(), new ZonasAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        ZonaVenta zv = varGlob.getListaZonas().get(position);
                        varGlob.setZonaActual(zv);
                        //Carga de las mesas en el adapter.
                        if (!zv.getListaMesas().isEmpty() && zv.getListaMesas() != null) {
                            mesasAdapter = new MesasAdapter(zv.getListaMesas(), new MesasAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    varGlob.setMesaActual(zv.getListaMesas().get(position));
                                    new CheckTicketTask(1).execute();
                                }

                                @Override
                                public void onItemLongClick(int position){
                                    varGlob.setMesaActual(zv.getListaMesas().get(position));
                                    int estadoMesa=varGlob.getMesaActual().getEstado();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MesasActivity.this);
                                    switch (estadoMesa){
                                        case 1:
                                            builder.setTitle("¿Quieres reservar la mesa?");
                                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    new CambiarEstadoMesas().execute(1);
                                                }
                                            });
                                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //Cancela y no pasa nada
                                                }
                                            });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                            break;
                                        case 2:
                                            Toast.makeText(MesasActivity.this,"Mesa ocupada",Toast.LENGTH_SHORT).show();
                                            break;
                                        case 3:
                                            builder.setTitle("¿Quieres quitar la reserva de la mesa?");
                                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    new CambiarEstadoMesas().execute(2);
                                                }
                                            });
                                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //Cancela y no pasa nada
                                                }
                                            });
                                            AlertDialog dialog2 = builder.create();
                                            dialog2.show();
                                            break;
                                    }
                                }
                            });
                            recyclerViewMesas.setAdapter(mesasAdapter);
                        } else {
                            mesasAdapter = new MesasAdapter(new ArrayList<>(), null);
                            recyclerViewMesas.setAdapter(mesasAdapter);
                            Toast.makeText(MesasActivity.this, "No se encontraron mesas para esta zona", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                recyclerViewZonas.setAdapter(zonasAdapter);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        comenzarRecargaEstadosDeMesa();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pararRecargaEstadosDeMesa();
    }

    //Arreglo para que cuando intentes ir hacia atras vaya a usuarios empezando la app de nuevo
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();  // Si quieres cerrar la actividad actual
    }

    private class CerrarSesionTask extends AsyncTask<Void,Void,Void>{

        protected Void doInBackground(Void... voids) {
            UsuariosBD usuariosBD = new UsuariosBD(varGlob.getConexionSQL());
            System.out.println(varGlob.getUsuarioActual().getId()+"____"+varGlob.getMacActual());
            usuariosBD.unsetActiveUser(varGlob.getUsuarioActual().getId(),varGlob.getMacActual());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "Sesion Cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MesasActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class CheckTicketTask extends AsyncTask<Void, Void, Ticket> {
        int idSerie;

        public CheckTicketTask(int idSerie) {
            this.idSerie = idSerie;
        }

        @Override
        protected Ticket doInBackground(Void... voids) {
            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
            // Verificar si ya existe un ticket para la mesa seleccionada
            Ticket existingTicket = ticketBD.getTicketForMesa(varGlob.getMesaActual().getId(), varGlob.getIdDispositivoActual(), varGlob.getSeccionIdUsuariosActual());
            return existingTicket;
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            varGlob.setTicketActual(ticket);
            if (ticket != null) {
                new CargarDetallesTaskYActualizaEscribiendo().execute();
            }else{
                Intent intent;
                intent = new Intent(MesasActivity.this, ComensalesActivity.class);
                ticket= new Ticket();
                ticket.setNuevo(true);
                varGlob.setTicketActual(ticket);

                startActivity(intent);
            }
        }
    }

    //AsyncTask para cargar los detalles en el ticket

    private class CargarDetallesTaskYActualizaEscribiendo extends AsyncTask<Void, Void, Ticket> {

        @Override
        protected Ticket doInBackground(Void... voids) {
            Ticket ticket=varGlob.getTicketActual();
            TicketBD ticketBD=new TicketBD(varGlob.getConexionSQL());
            if(!ticketBD.isEscribiendo(varGlob.getTicketActual().getId())){
                ticketBD.cargarDetallesEnTicket(ticket,MesasActivity.this);
                ticketBD.actualizaEscribiendo(true,varGlob.getTicketActual().getId());
                return ticket;
            }else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(Ticket ticket) {

            if (ticket!=null){
                Intent intent =new Intent(MesasActivity.this,FamiliasActivity.class);
                varGlob.setTicketActual(ticket);
                startActivity(intent);
            }else{
                Toast.makeText(MesasActivity.this,"El ticket esta ocupado",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CambiarEstadoMesas extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int estadoMesa = params[0];
            TicketBD ticketBD=new TicketBD(varGlob.getConexionSQL());
            if (estadoMesa==1){
                ticketBD.actualizaMesaAReservada(varGlob.getMesaActual().getId());
            }else{
                ticketBD.actualizaMesaALibre(varGlob.getMesaActual().getId());
            }
            new TicketBD(varGlob.getConexionSQL()).actualizarEstadoMesas(varGlob.getListaZonas());
            return estadoMesa;
        }

        @Override
        protected void onPostExecute(Integer resultado) {
            if(varGlob.getZonaActual()!=null&&mesasAdapter!=null){
                mesasAdapter.updateData(varGlob.getZonaActual().getListaMesas());
            }
        }
    }


    private void comenzarRecargaEstadosDeMesa() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new TicketBD(varGlob.getConexionSQL()).actualizarEstadoMesas(varGlob.getListaZonas());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Cambios en la UI
                        if(varGlob.getZonaActual()!=null&&mesasAdapter!=null){
                            System.out.println("Hecho");
                            mesasAdapter.updateData(varGlob.getZonaActual().getListaMesas());
                        }
                    }
                });
            }
        }, 0, 2, TimeUnit.SECONDS); // Inicia inmediatamente y repite cada 2 segundos
    }

    private void pararRecargaEstadosDeMesa() {
        if (scheduler != null) {
            scheduler.shutdown(); // Detiene la ejecución
        }
    }



}