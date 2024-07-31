package com.example.comandera;

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
import com.example.comandera.data.FamiliasBD;
import com.example.comandera.utils.Familia;
import com.example.comandera.utils.FichaPersonal;

import java.util.List;

public class FamiliasActivity extends AppCompatActivity {
    RecyclerView recyclerViewFamilias;
    FichaPersonal fichaPersonal;
    TextView tvUser;

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
        recyclerViewFamilias = findViewById(R.id.recyclerViewFamilias);
        tvUser = findViewById(R.id.tvUser);
        recyclerViewFamilias.setLayoutManager(new GridLayoutManager(this, 3));
        fichaPersonal = getIntent().getParcelableExtra("fichaPersonal");

        if(fichaPersonal != null){
            tvUser.setText("Comandera/ " +fichaPersonal.getUsuarioApp());
        }
        int zonaId = getIntent().getIntExtra("zonaId", -1);

        new GetVisibleFamilias().execute(zonaId);
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
                System.out.println(familias.size());
                FamiliasAdapter adapter = new FamiliasAdapter(FamiliasActivity.this, familias);
                recyclerViewFamilias.setAdapter(adapter);
            } else {
                Toast.makeText(FamiliasActivity.this, "No se encontraron familias visibles para esta zona", Toast.LENGTH_SHORT).show();
            }
        }
    }
}