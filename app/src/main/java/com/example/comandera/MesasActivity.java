package com.example.comandera;

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

import com.example.comandera.adapters.MesasAdapter;
import com.example.comandera.adapters.ZonasAdapter;
import com.example.comandera.data.MesasBD;
import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.data.UsuariosBD;
import com.example.comandera.data.ZonasVentaBD;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.Mesa;
import com.example.comandera.utils.ZonaVenta;

import java.util.ArrayList;
import java.util.List;

public class MesasActivity extends AppCompatActivity {
    FichaPersonal fichaPersonal;
    TextView tvUser;
    int seccionId;
    String zonaVenta;
    RecyclerView recyclerViewZonas, recyclerViewMesas;
    ZonasAdapter zonasAdapter;
    MesasAdapter mesasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mesas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvUser = findViewById(R.id.tvUser);
        recyclerViewZonas = findViewById(R.id.recyclerViewZonas);
        recyclerViewMesas = findViewById(R.id.recyclerViewMesas);

        recyclerViewZonas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMesas.setLayoutManager(new GridLayoutManager(this, 4));

        fichaPersonal = getIntent().getParcelableExtra("fichaPersonal");
        seccionId = getIntent().getIntExtra("seccionId", -1);

        if(fichaPersonal != null){
            tvUser.setText("Comandera/ " +fichaPersonal.getUsuarioApp());
            new GetZonas().execute(seccionId);
        }
    }
    private class GetZonas extends AsyncTask<Integer, Void, List<ZonaVenta>> {
        @Override
        protected List<ZonaVenta> doInBackground(Integer... params) {
            int seccionId = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(MesasActivity.this);
            ZonasVentaBD zonasBD = new ZonasVentaBD(sqlServerConnection);
            return zonasBD.getZonasBySeccionId(seccionId);
        }

        @Override
        protected void onPostExecute(List<ZonaVenta> zonasVenta) {
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
        int zonaId;
        @Override
        protected List<Mesa> doInBackground(Integer... params) {
            zonaId = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(MesasActivity.this);
            MesasBD mesasBD = new MesasBD(sqlServerConnection);
            return mesasBD.getMesasByZonaId(zonaId);
        }

        @Override
        protected void onPostExecute(List<Mesa> mesas) {
            if (!mesas.isEmpty() && mesas != null) {
                mesasAdapter = new MesasAdapter(mesas, new MesasAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String mesaNombre) {
                        Intent intent = new Intent(MesasActivity.this, ComensalesActivity.class);
                        intent.putExtra("mesaNombre", mesaNombre);
                        intent.putExtra("zonaVenta", zonaVenta);
                        intent.putExtra("zonaId", zonaId);
                        intent.putExtra("fichaPersonal", fichaPersonal);
                        startActivity(intent);
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
}