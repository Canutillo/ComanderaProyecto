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
import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.data.UsuariosBD;
import com.example.comandera.utils.DeviceInfo;
import com.example.comandera.utils.Dispositivo;
import com.example.comandera.utils.FichaPersonal;

import java.util.ArrayList;
import java.util.List;

public class ConfigActivity extends AppCompatActivity {
Spinner spinner;
Button bGuardarMAC;
String androidID;
int seccionID;

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
        spinner = findViewById(R.id.spinner);
        bGuardarMAC = findViewById(R.id.button);

        new GetDispositivosTask().execute();

        androidID = DeviceInfo.getAndroidID(this);

        bGuardarMAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dispositivo selectedDispositivo = (Dispositivo) spinner.getSelectedItem();
                if (selectedDispositivo != null) {
                    String macValue = androidID;
                    new UpdateMacTask().execute(selectedDispositivo.getId(), macValue);
                    new GetSeccionIdTask().execute(androidID);
                } else {
                    Toast.makeText(ConfigActivity.this, "No se ha seleccionado ningún dispositivo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // coger los dispositivos de nuestra base de datos que aun no tengan un dispositivo asociado (sin mac) para mostrarlos en el spinner
    private class GetDispositivosTask extends AsyncTask<Void, Void, List<Dispositivo>> {
        @Override
        protected List<Dispositivo> doInBackground(Void... voids) {
            SQLServerConnection sqlServerConnection = new SQLServerConnection(ConfigActivity.this);
            DispositivosBD dispositivosBD = new DispositivosBD(sqlServerConnection);
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
            SQLServerConnection sqlServerConnection = new SQLServerConnection(ConfigActivity.this);
            DispositivosBD dispositivosBD = new DispositivosBD(sqlServerConnection);
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
    private class GetSeccionIdTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String mac = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(ConfigActivity.this);
            DispositivosBD dispositivosBD = new DispositivosBD(sqlServerConnection);
            return dispositivosBD.getIdSeccion(mac);
        }

        @Override
        protected void onPostExecute(Integer seccionId) {
            seccionID = seccionId;
            if (seccionId != null) {
                new GetUsers().execute(seccionId);
            } else {
                Toast.makeText(ConfigActivity.this, "No se encontró la sección", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //para depués coger todos los usuarios que esten en esa seccion pasando el id de seccion cogido anteriomente
    private class GetUsers extends AsyncTask<Integer, Void, List<FichaPersonal>> {
        @Override
        protected List<FichaPersonal> doInBackground(Integer... params) {
            int seccionId = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(ConfigActivity.this);
            UsuariosBD usuariosBD = new UsuariosBD(sqlServerConnection);
            return usuariosBD.getUsers(seccionId);
        }

        @Override
        protected void onPostExecute(List<FichaPersonal> fichas) {
            if (!fichas.isEmpty()) {
                // Si hay usuarios vamos a la página de usuarios pasándole desde aqui la lista de usuarios
                Intent intent = new Intent(ConfigActivity.this, UsuariosActivity.class);
                intent.putParcelableArrayListExtra("listaUsuarios", new ArrayList<>(fichas));
                intent.putExtra("seccionId", seccionID);
                startActivity(intent);
            } else {
                // Si no hay usuarios que salte directamente a la pagina de las mesas
                Toast.makeText(ConfigActivity.this, "No se encontró ninguna coincidencia", Toast.LENGTH_SHORT).show();
            }
        }
    }
}