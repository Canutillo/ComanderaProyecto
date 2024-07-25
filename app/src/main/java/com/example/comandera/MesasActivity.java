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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.adapters.ZonasAdapter;
import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.data.UsuariosBD;
import com.example.comandera.data.ZonasVentaBD;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.ZonaVenta;

import java.util.ArrayList;
import java.util.List;

public class MesasActivity extends AppCompatActivity {
    FichaPersonal fichaPersonal;
    TextView tvUser;
    int seccionId;
    RecyclerView recyclerViewZonas;
    ZonasAdapter zonasAdapter;
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

        recyclerViewZonas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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
                zonasAdapter = new ZonasAdapter(zonasVenta);
                recyclerViewZonas.setAdapter(zonasAdapter);
            } else {
            }
        }
    }
}