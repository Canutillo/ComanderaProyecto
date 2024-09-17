package com.example.comandera;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
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
import com.example.comandera.utils.DetalleDocumento;
import com.example.comandera.utils.DeviceInfo;
import com.example.comandera.utils.Familia;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.Ticket;

import android.content.IntentFilter;

import android.content.BroadcastReceiver;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

public class FamiliasActivity extends AppCompatActivity {
    VariablesGlobales varGlob;
    RecyclerView recyclerViewFamilias, recyclerTicket;
    TextView tvUser;
    TicketAdapter ticketAdapter;

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
        //Pruebas a borrar
        Toast.makeText(FamiliasActivity.this,varGlob.getZonaActual().getIdTarifaVenta()+"",Toast.LENGTH_LONG).show();

        recyclerViewFamilias = findViewById(R.id.recyclerViewFamilias);
        LinearLayout includedLayout = findViewById(R.id.recyclerTicket);
        recyclerTicket = includedLayout.findViewById(R.id.recyclerViewTicket);
        tvUser = findViewById(R.id.tvUser);

        recyclerViewFamilias.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerTicket.setLayoutManager(new LinearLayoutManager(this));

        if (varGlob.getUsuarioActual() != null) {
            tvUser.setText("Comandera/ " + varGlob.getUsuarioActual().getUsuarioApp()+"/  "+varGlob.getMesaActual().getNombre());
        }
        //CODIGO CARLOTA A BORRAR
        //loadAndDisplayDescriptions();
        cargaTicket();
        new GetVisibleFamilias().execute(varGlob.getZonaActual().getId());
    }

    //NUEVO METODO PARA PINTAR EL TICKET
    public void cargaTicket(){
        if (varGlob.getTicketActual().getDetallesTicket() != null && !varGlob.getTicketActual().getDetallesTicket().isEmpty()) {
            if (ticketAdapter == null) {
                ticketAdapter = new TicketAdapter(FamiliasActivity.this, varGlob.getTicketActual().getDetallesTicket());
                recyclerTicket.setAdapter(ticketAdapter);
            } else {
                ticketAdapter.updateData(varGlob.getTicketActual().getDetallesTicket());
            }
        } else {
            Toast.makeText(FamiliasActivity.this, "No se encontraron detalles del documento.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MesasActivity.class);
        startActivity(intent);
        finish();  // Si quieres cerrar la actividad actual
    }

    // BroadcastReceiver para manejar actualizaciones de ticket desde otras activities
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.comandera.UPDATE_FAMILIAS")) {
                cargaTicket();
                Toast.makeText(FamiliasActivity.this, "Se ha actualizado el ticket", Toast.LENGTH_SHORT).show();
            }
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
    }


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

    //A partir de aqui es como funcionaba antes a Borrar
/*
    private void loadAndDisplayDescriptions() {
        new LoadTicketAndDescriptionsTask().execute(varGlob.getMesaActual().getId(), varGlob.getIdDispositivoActual(), varGlob.getSeccionIdUsuariosActual());
    }


    private class LoadTicketAndDescriptionsTask extends AsyncTask<Integer, Void, Ticket> {
        private int cabeceraId = -1;

        @Override
        protected Ticket doInBackground(Integer... params) {
            int mesaId = params[0];
            int dispositivoId = params[1];
            int seccionId = params[2];

            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
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
                    ticketAdapter = new TicketAdapter(FamiliasActivity.this, detalles);
                    recyclerTicket.setAdapter(ticketAdapter);
                } else {
                    ticketAdapter.updateData(detalles);
                }
            } else {
                Toast.makeText(FamiliasActivity.this, "No se encontraron detalles del documento.", Toast.LENGTH_SHORT).show();
            }
        }
    }
*/
}
