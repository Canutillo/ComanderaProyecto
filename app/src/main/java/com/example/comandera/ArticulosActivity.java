package com.example.comandera;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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

import com.example.comandera.adapters.ArticulosAdapter;
import com.example.comandera.adapters.OpcionesAdapter;
import com.example.comandera.adapters.TicketAdapter;
import com.example.comandera.data.ArticulosBD;
import com.example.comandera.data.PreguntasBD;
import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.Articulo;
import com.example.comandera.utils.DetalleDocumento;
import com.example.comandera.utils.PreguntaArticulo;
import com.example.comandera.utils.Ticket;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class ArticulosActivity extends AppCompatActivity {
    VariablesGlobales varGlob;
    RecyclerView recyclerViewArticulos, recyclerTicket;
    TextView tvText, tvUser;
    private Ticket existingTicket;
    TicketAdapter ticketAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        varGlob=(VariablesGlobales) getApplicationContext();

        System.out.println("Articulos");
        tvUser = findViewById(R.id.tvUser);
        tvText = findViewById(R.id.tvText);
        recyclerViewArticulos = findViewById(R.id.recyclerViewArticulos);

        LinearLayout includedLayout = findViewById(R.id.recyclerTicket);
        recyclerTicket = includedLayout.findViewById(R.id.recyclerViewTicket);

        recyclerViewArticulos.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerTicket.setLayoutManager(new LinearLayoutManager(this));

        if (varGlob.getUsuarioActual() != null) {
            tvUser.setText("Comandera/ " + varGlob.getUsuarioActual().getUsuarioApp()+"/  "+varGlob.getMesaActual().getNombre());
        }
        if(varGlob.getFamiliaActual().getNombre() != null){
            tvText.setText(varGlob.getFamiliaActual().getNombre());
        }

        new GetArticulos().execute();
        cargaTicket();
    }

    //METODO PARA MOSTRAR LOS DETALLES DEL TICKET
    public void cargaTicket(){
        if (varGlob.getTicketActual().getDetallesTicket() != null && !varGlob.getTicketActual().getDetallesTicket().isEmpty()) {
            if (ticketAdapter == null) {
                ticketAdapter = new TicketAdapter(ArticulosActivity.this, varGlob.getTicketActual().getDetallesTicket());
                recyclerTicket.setAdapter(ticketAdapter);
            } else {
                ticketAdapter.updateData(varGlob.getTicketActual().getDetallesTicket());
            }
        } else {
            Toast.makeText(ArticulosActivity.this, "Ticket vacio.", Toast.LENGTH_SHORT).show();
        }
        updateFamiliasActivity();
    }

    private void updateFamiliasActivity() {
        Intent intent = new Intent("com.example.comandera.UPDATE_FAMILIAS");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //OBTIENE LOS ARTICULOS Y GESTIONA EL ONCLICK
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
                varGlob.getTicketActual().anadirDetalleDocumentoVenta(varGlob.getArticuloActual(),varGlob.getTiposIVA(),varGlob.getTarifasDeVentas(),"",varGlob.getZonaActual().getIdTarifaVenta());
                Toast.makeText(ArticulosActivity.this,varGlob.getTicketActual().getDetallesTicket().toString(),Toast.LENGTH_LONG).show();
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
                                varGlob.getTicketActual().anadirDetalleDocumentoVenta(varGlob.getArticuloActual(),varGlob.getTiposIVA(),varGlob.getTarifasDeVentas(),opcionesSeleccionadas.toString(),varGlob.getZonaActual().getIdTarifaVenta());
                                cargaTicket();
                            }
                        });
                if(index>0){
                    builder.setNegativeButton("Anterior", (dialog, which) -> {
                        dialog.dismiss();
                        if (index > 0) {
                            showPreguntaDialog(preguntas, index - 1, opcionesSeleccionadas);
                        }else{
                            dialog.dismiss();
                        }
                    });
                }
                        builder.setCancelable(false)
                        .show();
            } else {
                Toast.makeText(ArticulosActivity.this, "No se encontraron opciones para esta pregunta", Toast.LENGTH_SHORT).show();
            }
        }
    }








    //NO SE USA
    private class AddArticuloToTicketTask extends AsyncTask<Articulo, Void, Ticket> {
        @Override
        protected Ticket doInBackground(Articulo... params) {
            Articulo articulo = params[0];
            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());

            if (existingTicket == null) {
                throw new IllegalStateException("No existe un ticket para agregar artículos.");
            }
            ticketBD.addDetalleDocumentoVenta(existingTicket.getId(), articulo.getId(), 1, articulo.getNombre(), articulo.getNombre(), varGlob.getZonaActual().getId());

            return existingTicket;
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            existingTicket = ticket;
            //actualizar la interfaz del ticket cuando añado un articulo
            /*new LoadDescripcionesLargasTask().execute(existingTicket.getId());*/
            new GetPreguntas().execute(varGlob.getArticuloActual().getId());
            /*updateFamiliasActivity();*/
        }
    }



    //NO SE USA
    private class CreateTicketTask extends AsyncTask<Void, Void, Ticket> {
        private Articulo articulo;

        public CreateTicketTask(Articulo articulo) {
            this.articulo = articulo;
        }

        @Override
        protected Ticket doInBackground(Void... voids) {
            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());

            // Crear un nuevo ticket
            //long newTicketId = ticketBD.createNewTicket(1 /*Ver lo de las series en el futuro*/, varGlob.getSeccionIdUsuariosActual(), varGlob.getIdDispositivoActual(), varGlob.getMesaActual().getId(), varGlob.getUsuarioActual().getId(), varGlob.getTicketActual().getComensales(), articulo.getId(), 1, articulo.getNombre(), articulo.getNombre());
            existingTicket = ticketBD.getTicketForMesa(varGlob.getMesaActual().getId(), varGlob.getIdDispositivoActual(), varGlob.getSeccionIdUsuariosActual());

            if (existingTicket != null) {
                // Agregar el artículo al ticket recién creado
                ticketBD.addDetalleDocumentoVenta(existingTicket.getId(), articulo.getId(), 1, articulo.getNombre(), articulo.getNombre(), varGlob.getZonaActual().getId());
            }

            return existingTicket;
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            existingTicket = ticket;

            /*if (existingTicket != null) {
                // Actualizar la interfaz para mostrar el nuevo artículo en el ticket
                new LoadDescripcionesLargasTask().execute(existingTicket.getId());
                updateFamiliasActivity();
            }*/

            // Cargar y mostrar preguntas asociadas al artículo
            new GetPreguntas().execute(articulo.getId());
            /*updateFamiliasActivity();*/
        }
    }


    //comprobar si ya existe un ticket, si ya existe te devuelve el ticket
    private class GetTicketTask extends AsyncTask<Void, Void, Ticket> {
        private int mesaId;
        private int dispositivoId;
        private int seccionId;

        public GetTicketTask(int mesaId, int dispositivoId, int seccionId) {
            this.mesaId = mesaId;
            this.dispositivoId = dispositivoId;
            this.seccionId = seccionId;
        }

        @Override
        protected Ticket doInBackground(Void... voids) {
            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
            return ticketBD.getTicketForMesa(mesaId, dispositivoId, seccionId);
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            existingTicket = ticket;

            if (existingTicket == null) {
                // Si no existe un ticket, crear uno y añadir el artículo
                new CreateTicketTask(varGlob.getArticuloActual()).execute();
            } else {
                // Si el ticket ya existe, agregar el artículo al ticket existente
                new AddArticuloToTicketTask().execute(varGlob.getArticuloActual());
            }
        }
    }

    //NO USADO

    //apartir de aqui los task son para mostrar los articulos en el ticket
    private class LoadDescripcionesLargasTask extends AsyncTask<Integer, Void, List<DetalleDocumento>> {
        @Override
        protected List<DetalleDocumento> doInBackground(Integer... params) {
            int cabeceraId = params[0];
            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
            return ticketBD.getDescripcionesLargasByCabeceraId(cabeceraId);
        }

        @Override
        protected void onPostExecute(List<DetalleDocumento> detalles) {
            if (detalles != null && !detalles.isEmpty()) {
                if (ticketAdapter == null) {
                    ticketAdapter = new TicketAdapter(ArticulosActivity.this, detalles);
                    recyclerTicket.setAdapter(ticketAdapter);
                } else {
                    ticketAdapter.updateData(detalles);
                }
            } else {
                Toast.makeText(ArticulosActivity.this, "No se encontraron detalles del documento.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadAndDisplayDescriptions() {
        new LoadTicketAndDescriptionsTask().execute(varGlob.getMesaActual().getId(), varGlob.getIdDispositivoActual(), varGlob.getSeccionIdUsuariosActual());
    }

    //NO USADO
    private class LoadTicketAndDescriptionsTask extends AsyncTask<Integer, Void, Ticket> {
        @Override
        protected Ticket doInBackground(Integer... params) {
            int mesaId = params[0];
            int dispositivoId = params[1];
            int seccionId = params[2];

            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
            Ticket ticket = ticketBD.getTicketForMesa(mesaId, dispositivoId, seccionId);


            return ticket;
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            if (ticket != null) {
                if (varGlob.getTicketActual().getId() > 0) {
                    new ArticulosActivity.LoadDescripcionesLargasTask().execute(varGlob.getTicketActual().getId());
                } else {
                    Toast.makeText(ArticulosActivity.this, "Error: Cabecera ID inválido.", Toast.LENGTH_SHORT).show();
                }
            } else {
            }
        }
    }
}
