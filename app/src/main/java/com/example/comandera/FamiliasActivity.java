package com.example.comandera;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.comandera.adapters.FamiliasAdapter;
import com.example.comandera.adapters.TicketAdapter;
import com.example.comandera.data.DispositivosBD;
import com.example.comandera.data.FamiliasBD;
import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.DeviceInfo;
import com.example.comandera.utils.Familia;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.Ticket;

import android.content.IntentFilter;

import android.content.BroadcastReceiver;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

public class FamiliasActivity extends AppCompatActivity {
    RecyclerView recyclerViewFamilias, recyclerTicket;
    FichaPersonal fichaPersonal;
    TextView tvUser;
    String androidID;
    int zonaId, mesaId, comensales, seccionId, dispositivoId, cabeceraId;
    Ticket ticket;
    TicketAdapter ticketAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_familias);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, new IntentFilter("com.example.comandera.UPDATE_FAMILIAS"));

        recyclerViewFamilias = findViewById(R.id.recyclerViewFamilias);
        recyclerTicket = findViewById(R.id.recyclerTicket);
        tvUser = findViewById(R.id.tvUser);

        recyclerViewFamilias.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerTicket.setLayoutManager(new LinearLayoutManager(this));

        fichaPersonal = getIntent().getParcelableExtra("fichaPersonal");
        ticket = getIntent().getParcelableExtra("ticket");
        seccionId = getIntent().getIntExtra("seccionId", -1);
        comensales = getIntent().getIntExtra("comensales", -1);
        androidID = DeviceInfo.getAndroidID(this);

        if (fichaPersonal != null) {
            tvUser.setText("Comandera/ " + fichaPersonal.getUsuarioApp());
        }

        zonaId = getIntent().getIntExtra("zonaId", -1);
        mesaId = getIntent().getIntExtra("mesaId", -1);

        // Ejecutar primero para obtener el dispositivoId y luego continuar con las demás tareas
        new GetDispositivoIdTask().execute(androidID);
        new GetVisibleFamilias().execute(zonaId);
    }

    // BroadcastReceiver para manejar actualizaciones de ticket
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.comandera.UPDATE_FAMILIAS")) {
                loadAndDisplayDescriptions();
                Toast.makeText(FamiliasActivity.this, "Se ha actualizado el ticket", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
    }
    private class LoadDescripcionesLargasTask extends AsyncTask<Integer, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Integer... params) {
            int cabeceraId = params[0];
            TicketBD ticketBD = new TicketBD(FamiliasActivity.this);
            return ticketBD.getDescripcionesLargasByCabeceraId(cabeceraId);
        }

        @Override
        protected void onPostExecute(List<String> descripcionesLargas) {
            if (descripcionesLargas != null && !descripcionesLargas.isEmpty()) {
                if (ticketAdapter == null) {
                    ticketAdapter = new TicketAdapter(FamiliasActivity.this, descripcionesLargas);
                    recyclerTicket.setAdapter(ticketAdapter);
                } else {
                    // Si el adaptador ya existe, actualizamos su lista
                    ticketAdapter.updateData(descripcionesLargas);
                }
            } else {
                Toast.makeText(FamiliasActivity.this, "No se encontraron descripciones largas.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetDispositivoIdTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String androidID = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(FamiliasActivity.this);
            DispositivosBD dispositivosBD = new DispositivosBD(sqlServerConnection);
            return dispositivosBD.getId(androidID);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null) {
                dispositivoId = result;

                // Llamar al método para cargar y mostrar descripciones una vez que dispositivoId esté disponible
                loadAndDisplayDescriptions();
            } else {
                Toast.makeText(FamiliasActivity.this, "Error: No se encontró el dispositivo.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void loadAndDisplayDescriptions() {
        new LoadTicketAndDescriptionsTask().execute(mesaId, dispositivoId, seccionId);
    }

    private class LoadTicketAndDescriptionsTask extends AsyncTask<Integer, Void, Ticket> {
        private int cabeceraId = -1;

        @Override
        protected Ticket doInBackground(Integer... params) {
            int mesaId = params[0];
            int dispositivoId = params[1];
            int seccionId = params[2];

            TicketBD ticketBD = new TicketBD(FamiliasActivity.this);
            Ticket ticket = ticketBD.getTicketForMesa(mesaId, dispositivoId, seccionId);

            if (ticket != null) {
                cabeceraId = ticket.getId();
            }

            return ticket;
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            if (ticket != null) {
                System.out.println("Cabecera ID: " + cabeceraId);

                if (cabeceraId > 0) {
                    new LoadDescripcionesLargasTask().execute(cabeceraId);
                } else {
                    Toast.makeText(FamiliasActivity.this, "Error: Cabecera ID inválido.", Toast.LENGTH_SHORT).show();
                }
            } else {
            }
        }
    }

    private class GetVisibleFamilias extends AsyncTask<Integer, Void, List<Familia>> {
        @Override
        protected List<Familia> doInBackground(Integer... params) {
            int zonaId = params[0];
            FamiliasBD familiasBD = new FamiliasBD(FamiliasActivity.this);
            return familiasBD.getVisibleFamilias(zonaId);
        }

        @Override
        protected void onPostExecute(List<Familia> familias) {
            if (familias != null && !familias.isEmpty()) {
                FamiliasAdapter adapter = new FamiliasAdapter(FamiliasActivity.this, familias, zonaId, fichaPersonal, mesaId, ticket, comensales, seccionId);
                recyclerViewFamilias.setAdapter(adapter);
            } else {
                Toast.makeText(FamiliasActivity.this, "No se encontraron familias visibles para esta zona", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
