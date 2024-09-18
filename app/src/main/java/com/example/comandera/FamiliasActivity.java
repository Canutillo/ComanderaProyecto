package com.example.comandera;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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
import com.example.comandera.data.FamiliasBD;
import com.example.comandera.data.TicketBD;
import com.example.comandera.utils.Familia;

import android.content.IntentFilter;

import android.content.BroadcastReceiver;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

public class FamiliasActivity extends AppCompatActivity {
    VariablesGlobales varGlob;
    RecyclerView recyclerViewFamilias, recyclerTicket;
    TextView tvUser;
    TicketAdapter ticketAdapter;
    ImageButton botonGuardar;

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

        recyclerViewFamilias = findViewById(R.id.recyclerViewFamilias);
        LinearLayout includedLayout = findViewById(R.id.recyclerTicket);
        recyclerTicket = includedLayout.findViewById(R.id.recyclerViewTicket);
        tvUser = findViewById(R.id.tvUser);
        botonGuardar=findViewById(R.id.guardar);

        recyclerViewFamilias.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerTicket.setLayoutManager(new LinearLayoutManager(this));

        if (varGlob.getUsuarioActual() != null) {
            tvUser.setText("Comandera/ " + varGlob.getUsuarioActual().getUsuarioApp()+"/  "+varGlob.getMesaActual().getNombre());
        }
        if(varGlob.getTicketActual().isNuevo()){
            new CreaTicket().execute();
        }else{
            cargaTicket();
        }
        new GetVisibleFamilias().execute(varGlob.getZonaActual().getId());

        botonGuardar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new ActualizaDetalles().execute();
            }
        });
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
            //Toast.makeText(FamiliasActivity.this, "Ticket vacio.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(varGlob.getTicketActual().getDetallesTicket().isEmpty()){
            new BorraTicket().execute();
            Intent intent = new Intent(FamiliasActivity.this, MesasActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(FamiliasActivity.this, MesasActivity.class);
            startActivity(intent);
            finish();
        }//CONTROLAR QUE SE GUARDE EL TICKET TAMBIEN
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

    private class CreaTicket extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TicketBD ticketBD=new TicketBD(varGlob.getConexionSQL());
            //Creamos el ticket nuevo con serie 1 ya que es la que hay por ahora
            long idNuevo = ticketBD.crearTicket(1,varGlob.getSeccionIdUsuariosActual(),varGlob.getIdDispositivoActual(),varGlob.getMesaActual().getId(),varGlob.getUsuarioActual().getId(),varGlob.getTicketActual().getComensales(),varGlob.getZonaActual().getId());
            varGlob.getTicketActual().setId((int) idNuevo);
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
            return ticketBD.borrarTicket(varGlob.getTicketActual().getId());
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                // Ticket borrado con éxito
                Toast.makeText(FamiliasActivity.this, "Ticket borrado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ActualizaDetalles extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TicketBD ticketBD = new TicketBD(varGlob.getConexionSQL());
            ticketBD.borrarDetalles(varGlob.getTicketActual().getId());
            ticketBD.actualizarTicket(varGlob.getTicketActual().getDetallesTicket(),varGlob.getTicketActual().getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onBackPressed();
        }
    }
}
