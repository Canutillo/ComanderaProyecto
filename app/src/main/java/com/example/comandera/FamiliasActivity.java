package com.example.comandera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.adapters.AnadirInterface;
import com.example.comandera.adapters.FamiliasAdapter;
import com.example.comandera.adapters.TicketAdapter;
import com.example.comandera.data.FamiliasBD;
import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.DetalleDocumento;
import com.example.comandera.utils.Familia;
import com.example.comandera.utils.Ticket;

import android.content.IntentFilter;

import android.content.BroadcastReceiver;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

public class FamiliasActivity extends AppCompatActivity implements AnadirInterface {
    VariablesGlobales varGlob;
    RecyclerView recyclerViewFamilias, recyclerTicket;
    TextView tvUser;
    TicketAdapter ticketAdapter;
    ImageButton botonCocina,botonBorrar,botonPagar;
    Spinner ordenPreparacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_familias);
        System.out.println("Familias");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        varGlob=(VariablesGlobales) getApplicationContext();
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, new IntentFilter("com.example.comandera.UPDATE_FAMILIAS"));


        ordenPreparacion = findViewById(R.id.ordenPreparacion);

        // Elementos del spinner
        String[] items = {"Sin orden","Bebidas", "Primeros", "Segundos", "Postres"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ordenPreparacion.setAdapter(adapter);
        ordenPreparacion.setSelection(varGlob.getOrdenPreparacionActual());
        ordenPreparacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                varGlob.setOrdenPreparacionActual(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerViewFamilias = findViewById(R.id.recyclerViewFamilias);
        LinearLayout includedLayout = findViewById(R.id.recyclerTicket);
        recyclerTicket = includedLayout.findViewById(R.id.recyclerViewTicket);
        tvUser = findViewById(R.id.tvUser);
        botonCocina=findViewById(R.id.mandarCocina);
        botonBorrar=findViewById(R.id.borrarTicket);
        botonPagar=findViewById(R.id.pagarTicket);

        recyclerViewFamilias.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerTicket.setLayoutManager(new LinearLayoutManager(this));

        //Carga de la barra con el nombre
        if (varGlob.getUsuarioActual() != null) {
            tvUser.setText("Comandera/ " + varGlob.getUsuarioActual().getUsuarioApp()+"/  "+varGlob.getMesaActual().getNombre());
        }
        if(varGlob.getTicketActual().isNuevo()){
            new CreaTicket().execute();
        }else{
            cargaTicket();
        }
        new GetVisibleFamilias().execute(varGlob.getZonaActual().getId());

        //Configuracion del boton de mandar a cocina
        botonCocina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizaDetallesYmandaCocina();
            }
        });

        //Configuracion del boton de borrar Ticket
        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FamiliasActivity.this);
                builder.setMessage("¿Quiere borrar el ticket?")
                        .setTitle("Borrar");
                // 3. Add buttons
                builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(FamiliasActivity.this);
                        builder2.setMessage("Por favor confirme la eliminacion del ticket")
                                .setTitle("Borrar");
                        // 3. Add buttons
                        builder2.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Cancela y no pasa nada
                            }
                        });
                        builder2.setNegativeButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new BorraTicket().execute();
                            }
                        });
                        AlertDialog dialog2 = builder2.create();
                        dialog2.show();
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
        //Configuracion del boton pagar ticket
        botonPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double sumatorio=0;
                for (DetalleDocumento detalle : varGlob.getTicketActual().getDetallesTicket()) {
                    // Suma el total de cada línea
                    sumatorio =sumatorio + detalle.getTotalLinea().doubleValue();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(FamiliasActivity.this);
                builder.setMessage("Total de ticket: "+sumatorio+" €")
                        .setTitle("Pagar ticket");
                // 3. Add buttons
                builder.setPositiveButton("Pagar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Metodo para pasar ticket a pagado
                        new MarcarTicketPagado().execute();
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

        //Metodo para la inactividad
        startInactivityTimer();
    }

    //Metodo para la inactividad
    private CountDownTimer inactivityTimer;

    private void startInactivityTimer() {
        inactivityTimer = new CountDownTimer(60000, 10000) {
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished<10000){
                    Toast.makeText(FamiliasActivity.this,"En 10 segundo se cerrará la app si no la usas",Toast.LENGTH_SHORT).show();
                }

            }

            public void onFinish() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Operaciones de base de datos
                        System.out.println("A eliminar");
                        if (varGlob.getTicketActual() != null) {
                            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
                            ticketBD.borrarDetalles(varGlob.getTicketActual().getId());
                            ticketBD.actualizarTicket(varGlob.getTicketActual().getDetallesTicket(),varGlob.getTicketActual().getId());
                            ticketBD.actualizaEscribiendo(false, varGlob.getTicketActual().getId());
                        }
                    }
                }).start();
                finishAffinity();
            }
        }.start();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetInactivityTimer();
    }

    private void resetInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.cancel(); // Cancela el temporizador anterior
        }
        startInactivityTimer(); // Inicia uno nuevo
    }

    @Override
    public void onStart(){
        super.onStart();
        ordenPreparacion.setSelection(varGlob.getOrdenPreparacionActual());
    }
    @Override
    public void onResume(){
        super.onResume();
        resetInactivityTimer();
    }

    @Override
    public void onPause(){
        super.onPause();
        if (inactivityTimer != null) {
            inactivityTimer.cancel(); // Cancela el temporizador si está en ejecución
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
    }

    //Trato del boton de añadir
    @Override
    public void onButton1Click(int position) {
        varGlob.getTicketActual().anadirUnidad(position);
        cargaTicket();
    }

    //Trato del boton de quitar
    @Override
    public void onButton2Click(int position) {
        varGlob.getTicketActual().quitarUnidad(position);
        cargaTicket();
    }





    //NUEVO METODO PARA PINTAR EL TICKET
    public void cargaTicket(){
        if (varGlob.getTicketActual().getDetallesTicket() != null && !varGlob.getTicketActual().getDetallesTicket().isEmpty()) {
            if (ticketAdapter == null) {
                ticketAdapter = new TicketAdapter(FamiliasActivity.this, varGlob.getTicketActual().getDetallesTicket(),this);
                recyclerTicket.setAdapter(ticketAdapter);
            } else {
                ticketAdapter.updateData(varGlob.getTicketActual().getDetallesTicket());

            }
        } else {
            if (ticketAdapter == null) {
                ticketAdapter = new TicketAdapter(FamiliasActivity.this, varGlob.getTicketActual().getDetallesTicket(),this);
                recyclerTicket.setAdapter(ticketAdapter);
            } else {
                ticketAdapter.updateData(varGlob.getTicketActual().getDetallesTicket());
            }
        }
        recyclerTicket.scrollToPosition(ticketAdapter.getItemCount()-1);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Si el ticket tiene algo te pregunta guardar o no y sale, si el ticket esta vacio lo borra y sale
        if(varGlob.getTicketActual().getDetallesTicket().isEmpty()){
            Toast.makeText(FamiliasActivity.this,"Ticket vacio, mesa ocupada",Toast.LENGTH_SHORT).show();
        }else{
            actualizaDetalles();
        }//CONTROLAR QUE SE GUARDE EL TICKET TAMBIEN Si tiene algo
        new CambiarEscribiendoFalso().execute();
    }

    // BroadcastReceiver para manejar actualizaciones de ticket desde otras activities
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.comandera.UPDATE_FAMILIAS")) {
                cargaTicket();
            }
        }
    };

    private class GetVisibleFamilias extends AsyncTask<Integer, Void, List<Familia>> {
        @Override
        protected List<Familia> doInBackground(Integer... params) {
            FamiliasBD familiasBD = new FamiliasBD(varGlob.getConexionSQL());
            return familiasBD.getVisibleFamilias(varGlob.getZonaActual().getId());
        }

        @Override
        protected void onPostExecute(List<Familia> familias) {
            if (familias != null && !familias.isEmpty()) {
                FamiliasAdapter adapter = new FamiliasAdapter(FamiliasActivity.this, familias, varGlob.getZonaActual().getId(), varGlob.getUsuarioActual(), varGlob.getMesaActual().getId(), varGlob.getTicketActual(), varGlob.getTicketActual().getComensales(), varGlob.getSeccionIdUsuariosActual(), varGlob);
                recyclerViewFamilias.setAdapter(adapter);
                //EL ON CLICK SE GESTIONA EN LA CLASE ADAPTER
            } else {
                Toast.makeText(FamiliasActivity.this, "No se encontraron familias visibles para esta zona", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CreaTicket extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TicketBD ticketBD=new TicketBD(varGlob.getConexionSQL());
            //Creamos el ticket nuevo con serie 1 ya que es la que hay por ahora
            long idNuevo = ticketBD.crearTicket(1,varGlob.getSeccionIdUsuariosActual(),varGlob.getIdDispositivoActual(),varGlob.getMesaActual().getId(),varGlob.getUsuarioActual().getId(),varGlob.getTicketActual().getComensales(),varGlob.getZonaActual().getId());
            varGlob.getTicketActual().setId((int) idNuevo);
            ticketBD.actualizaEscribiendo(true,varGlob.getTicketActual().getId());
            return null; // Retorna null porque es de tipo Void
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Aquí va el código que se ejecuta en la UI después de finalizar la tarea en segundo plano
            cargaTicket();
        }
    }

    private class BorraTicket extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
            return ticketBD.borrarTicket(varGlob.getTicketActual().getId(),varGlob.getMesaActual().getId());
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                // Ticket borrado con éxito
                Toast.makeText(FamiliasActivity.this, "Ticket borrado", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(FamiliasActivity.this, MesasActivity.class);
            startActivity(intent);
            finish();
        }
    }



    private class CambiarEscribiendoFalso extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
            ticketBD.actualizaEscribiendo(false,varGlob.getTicketActual().getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(FamiliasActivity.this, MesasActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class MarcarTicketPagado extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
            ticketBD.borrarDetalles(varGlob.getTicketActual().getId());
            ticketBD.actualizarTicket(varGlob.getTicketActual().getDetallesTicket(),varGlob.getTicketActual().getId());
            ticketBD.actualizaMesaALibre(varGlob.getMesaActual().getId());
            ticketBD.marcarTicketComoPagado(varGlob.getTicketActual().getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(FamiliasActivity.this, MesasActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void actualizaDetallesYmandaCocina() {
        if(!(varGlob.getTicketActual().getDetallesTicket().isEmpty())){
            Toast.makeText(FamiliasActivity.this,"Ticket enviado a cocina",Toast.LENGTH_SHORT).show();
            botonCocina.setEnabled(false);
            botonCocina.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C4C4C4")));
            Thread hilo = new Thread(new Runnable() {
                @Override
                public void run() {

                    TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
                    ticketBD.actualizaEscribiendo(false,varGlob.getTicketActual().getId());
                    ticketBD.borrarDetalles(varGlob.getTicketActual().getId());
                    ticketBD.actualizarTicket(varGlob.getTicketActual().getDetallesTicket(),varGlob.getTicketActual().getId());
                    ticketBD.mandarCocina(varGlob.getTicketActual().getId());
                    //Volver a mesas
                    Intent intent = new Intent(FamiliasActivity.this, MesasActivity.class);
                    startActivity(intent);
                    finish();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            botonCocina.setEnabled(true);
                            botonCocina.setBackgroundTintList(null);
                        }
                    });
                }
            });

            // Iniciar el hilo
            hilo.start();
        }else{
            Toast.makeText(FamiliasActivity.this,"Ticket vacio no se puede mandar a cocina",Toast.LENGTH_SHORT).show();
        }

    }

    private void actualizaDetalles() {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
                ticketBD.borrarDetalles(varGlob.getTicketActual().getId());
                ticketBD.actualizarTicket(varGlob.getTicketActual().getDetallesTicket(),varGlob.getTicketActual().getId());
            }
        });

        // Iniciar el hilo
        hilo.start();
    }





}


