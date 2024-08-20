package com.example.comandera;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.adapters.ArticulosAdapter;
import com.example.comandera.adapters.OpcionesAdapter;
import com.example.comandera.data.ArticulosBD;
import com.example.comandera.data.DispositivosBD;
import com.example.comandera.data.PreguntasBD;
import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.Articulo;
import com.example.comandera.utils.DeviceInfo;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.PreguntaArticulo;
import com.example.comandera.utils.Ticket;

import java.util.ArrayList;
import java.util.List;

public class ArticulosActivity extends AppCompatActivity {
    RecyclerView recyclerViewArticulos;
    int zonaId, comensales, familiaId;
    FichaPersonal fichaPersonal;
    TextView tvText, tvUser;
    Articulo art;
    String androidID;
    private Ticket existingTicket;
    private int mesaId, dispositivoId, seccionId, idSerie, idUsuarioTpv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulos);

        tvUser = findViewById(R.id.tvUser);
        tvText = findViewById(R.id.tvText);
        recyclerViewArticulos = findViewById(R.id.recyclerViewArticulos);

        androidID = DeviceInfo.getAndroidID(this);


        familiaId = getIntent().getIntExtra("familiaId", -1);
        String familiaNombre = getIntent().getStringExtra("familiaNombre");
        zonaId = getIntent().getIntExtra("zonaId", -1);
        mesaId = getIntent().getIntExtra("mesaId", -1);
        fichaPersonal = getIntent().getParcelableExtra("fichaPersonal");
        seccionId = getIntent().getIntExtra("seccionId", -1);
        idSerie = 1;
        idUsuarioTpv = fichaPersonal.getId();
        comensales = getIntent().getIntExtra("comensales", -1);
        recyclerViewArticulos.setLayoutManager(new GridLayoutManager(this, 4));

        if (fichaPersonal != null) {
            tvUser.setText("Comandera/ " + fichaPersonal.getUsuarioApp());
        }
        if(familiaNombre != null){
            tvText.setText(familiaNombre);
        }
        new GetDispositivoIdTask().execute(androidID);

        new GetArticulos().execute(familiaId);
    }

    private class GetArticulos extends AsyncTask<Integer, Void, List<Articulo>> {
        @Override
        protected List<Articulo> doInBackground(Integer... params) {
            int familiaId = params[0];
            ArticulosBD articulosBD = new ArticulosBD(ArticulosActivity.this);
            return articulosBD.getArticulos(familiaId, zonaId);
        }

        @Override
        protected void onPostExecute(List<Articulo> articulos) {
            if (articulos != null && !articulos.isEmpty()) {
                ArticulosAdapter adapter = new ArticulosAdapter(ArticulosActivity.this, articulos, new ArticulosAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Articulo articulo) {
                        art = articulo;
                        // Si no existe ticket, crear uno antes de continuar
                        new GetTicketTask(mesaId, dispositivoId, seccionId).execute();
                    }
                });
                recyclerViewArticulos.setAdapter(adapter);
            } else {
                Toast.makeText(ArticulosActivity.this, "No se encontraron artículos para esta familia", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetPreguntas extends AsyncTask<Integer, Void, List<PreguntaArticulo>> {
        @Override
        protected List<PreguntaArticulo> doInBackground(Integer... params) {
            int articuloId = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(ArticulosActivity.this);
            PreguntasBD preguntasBD = new PreguntasBD(sqlServerConnection);
            return preguntasBD.getPreguntas(articuloId);
        }

        @Override
        protected void onPostExecute(List<PreguntaArticulo> preguntas) {
            if (preguntas != null && !preguntas.isEmpty()) {
                showPreguntaDialog(preguntas, 0, new ArrayList<>());
            } else {
                Toast.makeText(ArticulosActivity.this, "No se encontraron preguntas para este artículo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPreguntaDialog(List<PreguntaArticulo> preguntas, int index, List<String> opcionesSeleccionadas) {
        PreguntaArticulo pregunta = preguntas.get(index);
        new GetOpciones(preguntas, index, opcionesSeleccionadas).execute(pregunta);
    }

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
            SQLServerConnection sqlServerConnection = new SQLServerConnection(ArticulosActivity.this);
            PreguntasBD preguntasBD = new PreguntasBD(sqlServerConnection);
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
                                Toast.makeText(ArticulosActivity.this, "Respuestas guardadas", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Anterior", (dialog, which) -> {
                            dialog.dismiss();
                            if (index > 0) {
                                showPreguntaDialog(preguntas, index - 1, opcionesSeleccionadas);
                            }
                        })
                        .setNeutralButton("Cancelar", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                Toast.makeText(ArticulosActivity.this, "No se encontraron opciones para esta pregunta", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //añadir un articulo al ticket, que en bbdd es añadri un fila a detalle_documentos_venta con el id de la cabecera
    private class AddArticuloToTicketTask extends AsyncTask<Articulo, Void, Ticket> {
        @Override
        protected Ticket doInBackground(Articulo... params) {
            Articulo articulo = params[0];
            TicketBD ticketBD = new TicketBD(ArticulosActivity.this);

            if (existingTicket == null) {
                throw new IllegalStateException("No existe un ticket para agregar artículos.");
            }

            ticketBD.addDetalleDocumentoVenta(existingTicket.getId(), articulo.getId(), 1, articulo.getNombre(), articulo.getNombre());
            return existingTicket;
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            existingTicket = ticket;
            new GetPreguntas().execute(art.getId());
        }
    }

    //crear el ticket
    private class CreateTicketTask extends AsyncTask<Void, Void, Ticket> {
        @Override
        protected Ticket doInBackground(Void... voids) {
            TicketBD ticketBD = new TicketBD(ArticulosActivity.this);

            // Verificar si existe un ticket para la mesa
            existingTicket = ticketBD.getTicketForMesa(mesaId, dispositivoId, seccionId);

            if (existingTicket == null) {
                // Crear un nuevo ticket si no existe
                long newTicketId = ticketBD.createNewTicket(idSerie, seccionId, dispositivoId, mesaId, idUsuarioTpv, comensales, art.getId(), 1, art.getNombre(), art.getNombre());
                existingTicket = ticketBD.getTicketForMesa(mesaId, dispositivoId, seccionId);
            }

            return existingTicket;
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            existingTicket = ticket;
            new GetPreguntas().execute(art.getId());
        }
    }

    //coger el id del dispositivo, necesario para la bbdd
    private class GetDispositivoIdTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String androidID = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(ArticulosActivity.this);
            DispositivosBD dispositivosBD = new DispositivosBD(sqlServerConnection);
            return dispositivosBD.getId(androidID);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null) {
                dispositivoId = result;
            } else {
                Toast.makeText(ArticulosActivity.this, "Error: No se encontró el dispositivo.", Toast.LENGTH_LONG).show();
                finish();
            }
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
            TicketBD ticketBD = new TicketBD(ArticulosActivity.this);
            return ticketBD.getTicketForMesa(mesaId, dispositivoId, seccionId);
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            existingTicket = ticket;

            if (existingTicket == null) {
                // Si no existe un ticket, crear uno antes de continuar
                new CreateTicketTask().execute();
            } else {
                // Si el ticket ya existe, agregar el artículo al ticket existente
                new AddArticuloToTicketTask().execute(art);
            }
        }
    }

}
