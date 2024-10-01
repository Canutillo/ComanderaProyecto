package com.example.comandera;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.comandera.data.UsuariosBD;
import com.example.comandera.utils.FichaPersonal;


public class UsuariosActivity extends AppCompatActivity implements FichaPersonalAdapter.OnItemClickListener {

    private VariablesGlobales varGlob;
    private RecyclerView recyclerViewUsuarios;
    private FichaPersonalAdapter adapter;
    private Button botonAjustes;

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
        varGlob=(VariablesGlobales) getApplicationContext();


        //Boton para desvincular dispositivo
        botonAjustes=findViewById(R.id.botonAjustes);
        botonAjustes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UsuariosActivity.this);
                builder.setMessage("¿Quiere desvincular este dispositivo?")
                        .setTitle("Ajustes");
                // 3. Add buttons
                builder.setPositiveButton("Desvincular", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DesvincularMac().execute();
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

        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);

        // Recibir la lista de usuarios

        if (varGlob.getListaUsuarios() != null && !varGlob.getListaUsuarios().isEmpty()) {
            // Configurar RecyclerView
            adapter = new FichaPersonalAdapter(varGlob.getListaUsuarios(), this);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            recyclerViewUsuarios.setLayoutManager(gridLayoutManager);
            recyclerViewUsuarios.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No se recibieron usuarios", Toast.LENGTH_SHORT).show();
        }
    }



    //Arreglo para que cuando intentes ir hacia atras reinicie la app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();  // Si quieres cerrar la actividad actual
    }

    @Override
    public void onItemClick(FichaPersonal fichaPersonal) {
        new CheckUserPasswordTask(fichaPersonal).execute();
    }

    private class CheckUserPasswordTask extends AsyncTask<Void, Void, Boolean> {

        public CheckUserPasswordTask(FichaPersonal fichaPersonal) {
            varGlob.setUsuarioActual(fichaPersonal);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            UsuariosBD usuariosBD = new UsuariosBD(varGlob.getConexionSQL());
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
            startActivity(intent);
            finish();
        }
    }

    private class DesvincularMac extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            UsuariosBD usuariosBD = new UsuariosBD(varGlob.getConexionSQL());
            usuariosBD.quitarMac(varGlob.getMacActual());
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "Dispositivo desvinculado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UsuariosActivity.this, ConfigActivity.class);
            startActivity(intent);
            finish();
        }
    }
}