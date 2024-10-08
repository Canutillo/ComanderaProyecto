package com.example.comandera;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.adapters.AnadirInterface;
import com.example.comandera.adapters.ArticulosAdapter;
import com.example.comandera.adapters.OpcionesAdapter;
import com.example.comandera.adapters.TicketAdapter;
import com.example.comandera.data.ArticulosBD;
import com.example.comandera.data.PreguntasBD;
import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.Articulo;
import com.example.comandera.utils.DetalleDocumento;
import com.example.comandera.utils.PreguntaArticulo;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class ArticulosActivity extends AppCompatActivity implements AnadirInterface {
    VariablesGlobales varGlob;
    RecyclerView recyclerViewArticulos, recyclerTicket;
    TextView tvText, tvUser;
    TicketAdapter ticketAdapter;
    Spinner ordenPreparacion;
    ImageButton botonCocina,botonBorrar,botonPagar,botonRepetirComanda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        varGlob = (VariablesGlobales) getApplicationContext();

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
        tvUser = findViewById(R.id.tvUser);
        tvText = findViewById(R.id.tvText);
        recyclerViewArticulos = findViewById(R.id.recyclerViewArticulos);
        botonCocina=findViewById(R.id.mandarCocina);
        botonBorrar=findViewById(R.id.borrarTicket);
        botonPagar=findViewById(R.id.pagarTicket);
        botonRepetirComanda=findViewById(R.id.repetirComanda);

        LinearLayout includedLayout = findViewById(R.id.recyclerTicket);
        recyclerTicket = includedLayout.findViewById(R.id.recyclerViewTicket);

        recyclerViewArticulos.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerTicket.setLayoutManager(new LinearLayoutManager(this));

        if (varGlob.getUsuarioActual() != null) {
            tvUser.setText("Comandera -> " + varGlob.getUsuarioActual().getUsuarioApp() + " -> " + varGlob.getMesaActual().getNombre());
        }
        if (varGlob.getFamiliaActual().getNombre() != null) {
            tvText.setText(varGlob.getFamiliaActual().getNombre());
        }



        //Configuracion del boton de borrar Ticket
        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ArticulosActivity.this);
                builder.setMessage("¿Quiere borrar el ticket?")
                        .setTitle("Borrar");
                // 3. Add buttons
                builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(ArticulosActivity.this);
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
                                varGlob.setGuardarYsalirFamiliasArticulos(false);
                                new BorraTicket().execute();
                            }
                        });
                        android.app.AlertDialog dialog2 = builder2.create();
                        dialog2.show();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Cancela y no pasa nada
                    }
                });
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Configuración boton repetir comanda
        botonRepetirComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DetalleDocumento> anadir=new ArrayList<>();
                for (DetalleDocumento detalle:varGlob.getTicketActual().getDetallesTicket()) {
                    if(detalle.isOtraRonda()){
                        anadir.add(new DetalleDocumento(detalle));
                        detalle.setOtraRonda(false);
                    }
                }
                for(DetalleDocumento detalle:anadir){
                    detalle.setEstadoComanda(0);
                    varGlob.getTicketActual().getDetallesTicket().add(detalle);
                }

                System.out.println(anadir.toString());
                cargaTicket();
                System.out.println(varGlob.getTicketActual().getDetallesTicket().toString());
            }
        });

        //Configuracion del boton de mandar a cocina
        botonCocina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                varGlob.setGuardarYsalirFamiliasArticulos(false);
                actualizaDetallesYmandaCocina();
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ArticulosActivity.this);
                builder.setMessage("Total de ticket: "+sumatorio+" €")
                        .setTitle("Pagar ticket");
                // 3. Add buttons
                builder.setPositiveButton("Pagar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Metodo para pasar ticket a pagado
                        varGlob.setGuardarYsalirFamiliasArticulos(false);
                        new MarcarTicketPagado().execute();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Cancela y no pasa nada
                    }
                });
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        new GetArticulos().execute();
        cargaTicket();
        //Metodo para la inactividad
        startInactivityTimer();


    }

    //Metodo para la inactividad
    private CountDownTimer inactivityTimer;

    private void startInactivityTimer() {
        inactivityTimer = new CountDownTimer(60000, 10000) {
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished<10000){
                    Toast.makeText(ArticulosActivity.this,"En 10 segundo se cerrará la app si no la usas.",Toast.LENGTH_SHORT).show();
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
    public void onResume(){
        super.onResume();
        resetInactivityTimer();
        varGlob.setGuardarYsalirFamiliasArticulos(true);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (inactivityTimer != null) {
            inactivityTimer.cancel(); // Cancela el temporizador si está en ejecución
        }
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    //Este es el metodo que android llama cuando te vas de un activity por decision propia, por interfaz, botones circulo y cuadrado (No la flecha para atras)
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if(varGlob.isGuardarYsalirFamiliasArticulos()){
            System.out.println("GuardarYSalir");
            actualizaDetallesYSale();
        }
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





    //METODO PARA MOSTRAR LOS DETALLES DEL TICKET
    public void cargaTicket() {
        if (varGlob.getTicketActual().getDetallesTicket() != null && !varGlob.getTicketActual().getDetallesTicket().isEmpty()) {
            if (ticketAdapter == null) {
                ticketAdapter = new TicketAdapter(ArticulosActivity.this, varGlob.getTicketActual().getDetallesTicket(), this);
                recyclerTicket.setAdapter(ticketAdapter);
            } else {
                ticketAdapter.updateData(varGlob.getTicketActual().getDetallesTicket());
            }
        } else {
            if (ticketAdapter == null) {
                ticketAdapter = new TicketAdapter(ArticulosActivity.this, varGlob.getTicketActual().getDetallesTicket(),this);
                recyclerTicket.setAdapter(ticketAdapter);
            } else {
                ticketAdapter.updateData(varGlob.getTicketActual().getDetallesTicket());
            }
        }
        updateFamiliasActivity();
        recyclerTicket.scrollToPosition(ticketAdapter.getItemCount()-1);
    }

    private void updateFamiliasActivity() {
        Intent intent = new Intent("com.example.comandera.UPDATE_FAMILIAS");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
            Intent intent = new Intent(ArticulosActivity.this, MesasActivity.class);
            startActivity(intent);
            finish();
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
                Toast.makeText(ArticulosActivity.this, "Ticket borrado", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(ArticulosActivity.this, MesasActivity.class);
            startActivity(intent);
            finish();
        }
    }


    //OBTIENE LOS ARTICULOS Y GESTIONA EL ONCLICK del recycler view de los articulos
    private class GetArticulos extends AsyncTask<Integer, Void, List<Articulo>> {
        @Override
        protected List<Articulo> doInBackground(Integer... params) {
            ArticulosBD articulosBD = new ArticulosBD(ArticulosActivity.this);
            return articulosBD.getArticulos(varGlob.getFamiliaActual().getId(), varGlob.getZonaActual().getId());
        }

        @Override
        protected void onPostExecute(List<Articulo> articulos) {
            if (articulos != null && !articulos.isEmpty()) {
                ArticulosAdapter adapter = new ArticulosAdapter(ArticulosActivity.this, articulos, new ArticulosAdapter.OnItemClickListener() {
                    //Cuando pinche en un articulo se añaden las cosas en el ticket local y tengo que actualizar el broadcaster de familias activity
                    @Override
                    public void onItemClick(Articulo articulo) {
                        varGlob.setArticuloActual(articulo);
                        new GetPreguntas().execute();
                    }
                });
                recyclerViewArticulos.setAdapter(adapter);
            } else {
                Toast.makeText(ArticulosActivity.this, "No se encontraron artículos para esta familia", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //COGE LAS PREGUNTAS QUE TIENE CADA ARTICULO Y AÑADE LOS QUE NO TIENE PREGUNTAS
    private class GetPreguntas extends AsyncTask<Integer, Void, List<PreguntaArticulo>> {
        @Override
        protected List<PreguntaArticulo> doInBackground(Integer... params) {
            PreguntasBD preguntasBD = new PreguntasBD(varGlob.getConexionSQL());
            return preguntasBD.getPreguntas(varGlob.getArticuloActual().getId());
        }

        @Override
        protected void onPostExecute(List<PreguntaArticulo> preguntas) {
            if (preguntas != null && !preguntas.isEmpty()) {
                showPreguntaDialog(preguntas, 0, new ArrayList<>());
            } else {
                //AÑADIR PRODUCTOS SIN PREGUNTAS
                varGlob.getTicketActual().anadirDetalleDocumentoVenta(varGlob.getArticuloActual(), varGlob.getTiposIVA(), varGlob.getTarifasDeVentas(), "", varGlob.getZonaActual().getIdTarifaVenta(),varGlob.getOrdenPreparacionActual(),varGlob.isNuevaEntrada());
                varGlob.setNuevaEntrada(false);
                cargaTicket();
            }
        }
    }

    private void showPreguntaDialog(List<PreguntaArticulo> preguntas, int index, List<String> opcionesSeleccionadas) {
        PreguntaArticulo pregunta = preguntas.get(index);
        new GetOpciones(preguntas, index, opcionesSeleccionadas).execute(pregunta);
    }

    //MUESTRA LAS OPCIONES Y AÑADE LAS RESPUESTAS COMO DESCRIPCION LARGA
    private class GetOpciones extends AsyncTask<PreguntaArticulo, Void, List<String>> {
        private List<PreguntaArticulo> preguntas;
        private int index;
        private PreguntaArticulo pregunta;
        private List<String> opcionesSeleccionadas;

        public GetOpciones(List<PreguntaArticulo> preguntas, int index, List<String> opcionesSeleccionadas) {
            this.preguntas = preguntas;
            this.index = index;
            this.opcionesSeleccionadas = opcionesSeleccionadas;
        }

        @Override
        protected List<String> doInBackground(PreguntaArticulo... params) {
            pregunta = params[0];
            PreguntasBD preguntasBD = new PreguntasBD(varGlob.getConexionSQL());
            return preguntasBD.getOpciones(pregunta.getId());
        }

        @Override
        protected void onPostExecute(List<String> opciones) {
            if (opciones != null && !opciones.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ArticulosActivity.this);
                builder.setTitle(pregunta.getTexto());

                View dialogView = getLayoutInflater().inflate(R.layout.dialog_preguntas, null);
                RecyclerView recyclerViewOpciones = dialogView.findViewById(R.id.recyclerViewOpciones);

                OpcionesAdapter adapter = new OpcionesAdapter(ArticulosActivity.this, opciones, opcionesSeleccionadas);
                recyclerViewOpciones.setLayoutManager(new LinearLayoutManager(ArticulosActivity.this));
                recyclerViewOpciones.setAdapter(adapter);

                builder.setView(dialogView)
                        .setPositiveButton(index < preguntas.size() - 1 ? "Siguiente" : "Guardar", (dialog, which) -> {
                            dialog.dismiss();
                            if (index < preguntas.size() - 1) {
                                showPreguntaDialog(preguntas, index + 1, opcionesSeleccionadas);
                            } else {
                                //AÑADIR PRODUCTOS CON PREGUNTAS
                                varGlob.getTicketActual().anadirDetalleDocumentoVenta(varGlob.getArticuloActual(), varGlob.getTiposIVA(), varGlob.getTarifasDeVentas(), opcionesSeleccionadas.toString(), varGlob.getZonaActual().getIdTarifaVenta(),varGlob.getOrdenPreparacionActual(),varGlob.isNuevaEntrada());
                                varGlob.setNuevaEntrada(false);
                                cargaTicket();
                            }
                        });
                if (index > 0) {
                    builder.setNegativeButton("Anterior", (dialog, which) -> {
                        dialog.dismiss();
                        if (index > 0) {
                            showPreguntaDialog(preguntas, index - 1, opcionesSeleccionadas);
                        } else {
                            dialog.dismiss();
                        }
                    });
                }
                builder.setCancelable(true)
                        .show();
            } else {
                Toast.makeText(ArticulosActivity.this, "No se encontraron opciones para esta pregunta", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void actualizaDetallesYmandaCocina() {
        if(!(varGlob.getTicketActual().getDetallesTicket().isEmpty())){
            boolean algoPorMandar=false;
            for (DetalleDocumento detalle:varGlob.getTicketActual().getDetallesTicket()) {
                if(detalle.getEstadoComanda()==0){
                    algoPorMandar=true;
                    break;
                }
            }
            if(algoPorMandar){
                Toast.makeText(ArticulosActivity.this,"Ticket enviado a cocina",Toast.LENGTH_SHORT).show();
                botonCocina.setEnabled(false);
                botonCocina.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C4C4C4")));
                Thread hilo = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
                        ticketBD.actualizaEscribiendo(false,varGlob.getTicketActual().getId());
                        ticketBD.borrarDetalles(varGlob.getTicketActual().getId());
                        ticketBD.actualizarTicketMandarCocina(varGlob.getTicketActual().getDetallesTicket(),varGlob.getTicketActual().getId());
                        ticketBD.mandarCocina(varGlob.getTicketActual().getId());
                        //Volver a mesas
                        Intent intent = new Intent(ArticulosActivity.this, MesasActivity.class);
                        startActivity(intent);
                        finish();
                        try {
                            Thread.sleep(500);
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
                Toast.makeText(ArticulosActivity.this,"Nada por mandar a cocina",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(ArticulosActivity.this,"Ticket vacio no se puede mandar a cocina",Toast.LENGTH_SHORT).show();
        }

    }


    private void actualizaDetallesYSale() {
        if(!(varGlob.getTicketActual().getDetallesTicket().isEmpty())){
            Thread hilo = new Thread(new Runnable() {
                @Override
                public void run() {
                    TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
                    ticketBD.actualizaEscribiendo(false,varGlob.getTicketActual().getId());
                    ticketBD.borrarDetalles(varGlob.getTicketActual().getId());
                    ticketBD.actualizarTicket(varGlob.getTicketActual().getDetallesTicket(),varGlob.getTicketActual().getId());
                    //Volver a mesas
                    finishAffinity();
                }
            });
            // Iniciar el hilo
            hilo.start();
        }
    }
}