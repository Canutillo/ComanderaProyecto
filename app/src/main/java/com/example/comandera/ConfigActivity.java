package com.example.comandera;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comandera.data.DispositivosBD;
import com.example.comandera.data.MesasBD;
import com.example.comandera.data.UsuariosBD;
import com.example.comandera.data.ZonasVentaBD;
import com.example.comandera.utils.Dispositivo;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.ZonaVenta;

import java.util.List;

public class ConfigActivity extends AppCompatActivity {
    private VariablesGlobales varGlob;
    Spinner spinner;
    Button bGuardarMAC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        varGlob=(VariablesGlobales) getApplicationContext();

        spinner = findViewById(R.id.spinner);
        bGuardarMAC = findViewById(R.id.button);

        new GetDispositivosTask().execute();

        bGuardarMAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dispositivo selectedDispositivo = (Dispositivo) spinner.getSelectedItem();
                if (selectedDispositivo != null) {
                    new UpdateMacTask().execute(selectedDispositivo.getId(), varGlob.getMacActual());
                    new GetSeccionYDispositivoIdTask().execute();
                } else {
                    Toast.makeText(ConfigActivity.this, "No se ha seleccionado ningún dispositivo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Arreglo para que cuando intentes ir hacia atras reinicie la app
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();  // Si quieres cerrar la actividad actual
    }

    // coger los dispositivos de nuestra base de datos que aun no tengan un dispositivo asociado (sin mac) para mostrarlos en el spinner
    private class GetDispositivosTask extends AsyncTask<Void, Void, List<Dispositivo>> {
        @Override
        protected List<Dispositivo> doInBackground(Void... voids) {
            DispositivosBD dispositivosBD = new DispositivosBD(varGlob.getConexionSQL());
            return dispositivosBD.getDispositivos();
        }

        @Override
        protected void onPostExecute(List<Dispositivo> dispositivos) {
            super.onPostExecute(dispositivos);

            // Crear un ArrayAdapter usando el diseño personalizado para los elementos del Spinner
            ArrayAdapter<Dispositivo> adapter = new ArrayAdapter<>(ConfigActivity.this,
                    android.R.layout.simple_spinner_item,
                    dispositivos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Configurar el Spinner con el adaptador
            spinner.setAdapter(adapter);
        }
    }

    // y poder seleccionar nuestros elementos (dispositivos) del spinner para guardarles la mac del dispositivo
    private class UpdateMacTask extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            int id = (int) params[0];
            String mac = (String) params[1];
            DispositivosBD dispositivosBD = new DispositivosBD(varGlob.getConexionSQL());
            return dispositivosBD.updateMac(id, mac);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Toast.makeText(ConfigActivity.this, "MAC actualizada exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ConfigActivity.this, "Error al actualizar la MAC", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // cojo el id de la seccion que tenga nuestro dispositivo
    private class GetSeccionYDispositivoIdTask extends AsyncTask<String, Void, int[]> {
        @Override
        protected int[] doInBackground(String... params) {
            DispositivosBD dispositivosBD = new DispositivosBD(varGlob.getConexionSQL());
            return dispositivosBD.getIdSeccionYDispositivo(varGlob.getMacActual());
        }

        @Override
        protected void onPostExecute(int[] vector) {
            varGlob.setIdDispositivoActual(vector[0]);
            varGlob.setSeccionIdUsuariosActual(vector[1]);
            new GetUsersZonasMesas().execute();
        }
    }

    //para depués coger todos los usuarios que esten en esa seccion pasando el id de seccion cogido anteriomente
    private class GetUsersZonasMesas extends AsyncTask<Integer, Void, List<FichaPersonal>> {
        @Override
        protected List<FichaPersonal> doInBackground(Integer... params) {
            UsuariosBD usuariosBD = new UsuariosBD(varGlob.getConexionSQL());
            ZonasVentaBD zonasVentaBD= new ZonasVentaBD(varGlob.getConexionSQL());
            MesasBD mesasBD=new MesasBD(varGlob.getConexionSQL());
            varGlob.setListaZonas(zonasVentaBD.getZonasBySeccionId(varGlob.getSeccionIdUsuariosActual()));
            for (ZonaVenta item : varGlob.getListaZonas()) {
                item.setListaMesas(mesasBD.getMesasByZonaId(item.getId()));
            }
            return usuariosBD.getUsers(varGlob.getSeccionIdUsuariosActual());
        }

        @Override
        protected void onPostExecute(List<FichaPersonal> fichas) {
            varGlob.setListaUsuarios(fichas);
            if (!varGlob.getListaUsuarios().isEmpty()) {
                // Si hay usuarios vamos a la página de usuarios pasándole desde aqui la lista de usuarios
                Intent intent = new Intent(ConfigActivity.this, UsuariosActivity.class);
                startActivity(intent);
            } else {
                // Si no hay usuarios que salte directamente a la pagina de las mesas
                Toast.makeText(ConfigActivity.this, "No se encontró ninguna coincidencia", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ConfigActivity.this, MesasActivity.class);
                startActivity(intent);
            }
        }
    }
}