package com.example.comandera;


import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.os.AsyncTask;
import android.content.Intent;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comandera.adapters.FichaPersonalAdapter;
import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.data.UsuariosBD;
import com.example.comandera.utils.FichaPersonal;

import java.util.List;

public class UsuariosActivity extends AppCompatActivity implements FichaPersonalAdapter.OnItemClickListener {

    private RecyclerView recyclerViewUsuarios;
    private FichaPersonalAdapter adapter;
    private List<FichaPersonal> listaUsuarios;
    int seccionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usuarios);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.usuarios), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);

        // Recibir la lista de usuarios
        listaUsuarios = getIntent().getParcelableArrayListExtra("listaUsuarios");
        seccionId = getIntent().getIntExtra("seccionId", -1);

        if (listaUsuarios != null && !listaUsuarios.isEmpty()) {
            // Configurar RecyclerView
            adapter = new FichaPersonalAdapter(listaUsuarios, this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            recyclerViewUsuarios.setLayoutManager(gridLayoutManager);
            recyclerViewUsuarios.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No se recibieron usuarios", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(FichaPersonal fichaPersonal) {
        new CheckUserPasswordTask(fichaPersonal).execute();
    }

    private class CheckUserPasswordTask extends AsyncTask<Void, Void, Boolean> {
        private final FichaPersonal fichaPersonal;

        public CheckUserPasswordTask(FichaPersonal fichaPersonal) {
            this.fichaPersonal = fichaPersonal;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            SQLServerConnection sqlServerConnection = new SQLServerConnection(UsuariosActivity.this);
            UsuariosBD usuariosBD = new UsuariosBD(sqlServerConnection);
            return usuariosBD.existsContrasena();
        }

        @Override
        protected void onPostExecute(Boolean exists) {
            Intent intent;
            // Si existe algún usuario con contraseña irá a la actividad de contraseña, sino a la actividad para seleccionar la mesa
            if (exists) {
                intent = new Intent(UsuariosActivity.this, ContrasenaActivity.class);
            } else {
                intent = new Intent(UsuariosActivity.this, MesasActivity.class);
            }
            intent.putExtra("fichaPersonal", fichaPersonal);
            intent.putExtra("seccionId", seccionId);
            startActivity(intent);
            finish();
        }
    }
}