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
import com.example.comandera.data.DispositivosBD;
import com.example.comandera.data.MesasBD;
import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.data.TicketBD;
import com.example.comandera.data.UsuariosBD;
import com.example.comandera.data.ZonasVentaBD;
import com.example.comandera.utils.DeviceInfo;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.Mesa;
import com.example.comandera.utils.Ticket;
import com.example.comandera.utils.ZonaVenta;

import java.util.ArrayList;
import java.util.List;

public class MesasActivity extends AppCompatActivity {
    FichaPersonal fichaPersonal;
    TextView tvUser;
    int seccionId, zonaId, dispositivoId, mesaId;
    String zonaVenta, nombreMesa, androidID;
    RecyclerView recyclerViewZonas, recyclerViewMesas;
    ZonasAdapter zonasAdapter;
    MesasAdapter mesasAdapter;
    Ticket existingTicket;
    private Button botonCerrarSesion;

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

        fichaPersonal = getIntent().getParcelableExtra("fichaPersonal");
        seccionId = getIntent().getIntExtra("seccionId", -1);

        if(fichaPersonal != null){
            tvUser.setText("Comandera/ " +fichaPersonal.getUsuarioApp());
            new GetZonas().execute(seccionId);
        }

        // Obtener el ANDROID_ID
        androidID = DeviceInfo.getAndroidID(this);

        new GetIdTask().execute(androidID);
    }

    //Arreglo para que cuando intentes ir hacia atras vaya a usuarios empezando la app de nuevo
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();  // Si quieres cerrar la actividad actual
    }

    private class GetIdTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String mac = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(MesasActivity.this);
            DispositivosBD dispositivosBD = new DispositivosBD(sqlServerConnection);
            return dispositivosBD.getId(mac);
        }

        @Override
        protected void onPostExecute(Integer id) {
            if (id != null) {
                dispositivoId = id;
            }
        }
    }

    private class CerrarSesionTask extends AsyncTask<Void,Void,Void>{

        protected Void doInBackground(Void... voids) {
            SQLServerConnection sqlServerConnection = new SQLServerConnection(MesasActivity.this);
            UsuariosBD usuariosBD = new UsuariosBD(sqlServerConnection);
            System.out.println(fichaPersonal.getId()+"____"+androidID);
            usuariosBD.unsetActiveUser(fichaPersonal.getId(),androidID);
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

    private class GetZonas extends AsyncTask<Integer, Void, List<ZonaVenta>> {
        @Override
        protected List<ZonaVenta> doInBackground(Integer... params) {
            seccionId = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(MesasActivity.this);
            ZonasVentaBD zonasBD = new ZonasVentaBD(sqlServerConnection);
            return zonasBD.getZonasBySeccionId(seccionId);
        }

        @Override
        protected void onPostExecute(List<ZonaVenta> zonasVenta) {
            //Carga del recycler view de las zonas.
            if (!zonasVenta.isEmpty()) {
                zonasAdapter = new ZonasAdapter(zonasVenta, new ZonasAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int zonaId) {
                        ZonaVenta zv = zonasVenta.get(zonaId-1);
                        zonaVenta = zv.getZona();

                        new GetMesas().execute(zonaId);
                    }
                });
                recyclerViewZonas.setAdapter(zonasAdapter);
            }
        }
    }

    private class GetMesas extends AsyncTask<Integer, Void, List<Mesa>> {
        @Override
        protected List<Mesa> doInBackground(Integer... params) {
            zonaId = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(MesasActivity.this);
            MesasBD mesasBD = new MesasBD(sqlServerConnection);
            return mesasBD.getMesasByZonaId(zonaId);
        }

        @Override
        protected void onPostExecute(List<Mesa> mesas) {
            //Carga del recycler view de las mesas y control de tickets existentes.
            if (!mesas.isEmpty() && mesas != null) {
                mesasAdapter = new MesasAdapter(mesas, new MesasAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String mesaNombre, int mesaId) {
                        nombreMesa = mesaNombre;
                        new CheckTicketTask(mesaId, dispositivoId, seccionId, 1, fichaPersonal.getId()).execute();
                    }
                });
                recyclerViewMesas.setAdapter(mesasAdapter);
            } else {
                mesasAdapter = new MesasAdapter(new ArrayList<>(), null);
                recyclerViewMesas.setAdapter(mesasAdapter);
                Toast.makeText(MesasActivity.this, "No se encontraron mesas para esta zona", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CheckTicketTask extends AsyncTask<Void, Void, Ticket> {
        int mesaId, dispositivoId, seccionId, idSerie, idUsuarioTpv;

        public CheckTicketTask(int mesaId, int dispositivoId, int seccionId, int idSerie, int idUsuarioTpv) {
            this.mesaId = mesaId;
            this.dispositivoId = dispositivoId;
            this.seccionId = seccionId;
            this.idSerie = idSerie;
            this.idUsuarioTpv = idUsuarioTpv;
        }

        @Override
        protected Ticket doInBackground(Void... voids) {
            TicketBD ticketBD = new TicketBD(MesasActivity.this);
            // Verificar si ya existe un ticket para la mesa seleccionada
            Ticket existingTicket = ticketBD.getTicketForMesa(mesaId, dispositivoId, seccionId);
            return existingTicket;
        }

        @Override
        protected void onPostExecute(Ticket ticket) {
            Intent intent, i;
            existingTicket = ticket;
            if (ticket != null) {
                intent = new Intent(MesasActivity.this, FamiliasActivity.class);
            }else{
                intent = new Intent(MesasActivity.this, ComensalesActivity.class);
            }

            intent.putExtra("mesaId", mesaId);
            intent.putExtra("zonaVenta", zonaVenta);
            intent.putExtra("mesaNombre", nombreMesa);
            intent.putExtra("zonaId", zonaId);
            intent.putExtra("seccionId", seccionId);
            intent.putExtra("fichaPersonal", fichaPersonal);
            intent.putExtra("dispositivoId", dispositivoId);
            startActivity(intent);
        }
    }
}