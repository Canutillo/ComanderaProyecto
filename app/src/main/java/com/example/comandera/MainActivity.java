package com.example.comandera;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
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
import com.example.comandera.utils.FichaPersonal;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
String androidID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Comandera);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Obtener el ANDROID_ID
        androidID = DeviceInfo.getAndroidID(this);

        new getMACS().execute();
    }
    //comprobar si la mac de nuestro dispositivio está en nuestra base de datos
    private class getMACS extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            SQLServerConnection sqlServerConnection = new SQLServerConnection(MainActivity.this);
            DispositivosBD dispositivosBD = new DispositivosBD(sqlServerConnection);
            return dispositivosBD.checkIfMacExists(androidID);
        }
        @Override
        protected void onPostExecute(Boolean exists) {
            if (!exists) {
                // Si la mac no esta en nuestra base de datos, tendremos que grabar la mac en la bbdd con la interfaz Config
                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                startActivity(intent);
            } else {

                new GetSeccionIdTask().execute(androidID);
            }
        }
    }

    // cojo el id de la seccion que tenga nuestro dispositivo
    private class GetSeccionIdTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String mac = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(MainActivity.this);
            DispositivosBD dispositivosBD = new DispositivosBD(sqlServerConnection);
            return dispositivosBD.getIdSeccion(mac);
        }

        @Override
        protected void onPostExecute(Integer seccionId) {
            if (seccionId != null) {
                new GetActiveUser().execute(seccionId);
            } else {
                Toast.makeText(MainActivity.this, "No se encontró la sección", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //para depués coger todos los usuarios que esten en esa seccion pasando el id de seccion cogido anteriomente
    private class GetUsers extends AsyncTask<Integer, Void, List<FichaPersonal>> {
        int seccionId;
        @Override
        protected List<FichaPersonal> doInBackground(Integer... params) {
            seccionId = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(MainActivity.this);
            UsuariosBD usuariosBD = new UsuariosBD(sqlServerConnection);
            return usuariosBD.getUsers(seccionId);
        }

        @Override
        protected void onPostExecute(List<FichaPersonal> fichas) {
            if (!fichas.isEmpty()) {
                // Si hay usuarios vamos a la página de usuarios pasándole desde aqui la lista de usuarios
                Intent intent = new Intent(MainActivity.this, UsuariosActivity.class);
                intent.putParcelableArrayListExtra("listaUsuarios", new ArrayList<>(fichas));
                intent.putExtra("seccionId", seccionId);
                startActivity(intent);
            } else {
                // Si no hay usuarios que salte directamente a la pagina de las mesas
                Intent intent = new Intent(MainActivity.this, MesasActivity.class);
                intent.putExtra("seccionId", seccionId);
                startActivity(intent);
            }
        }
    }

    // Obtener el usuario activo de la sección
    private class GetActiveUser extends AsyncTask<Integer, Void, FichaPersonal> {
        int seccionId;
        @Override
        protected FichaPersonal doInBackground(Integer... params) {
            seccionId = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(MainActivity.this);
            UsuariosBD usuariosBD = new UsuariosBD(sqlServerConnection);
            return usuariosBD.getActiveUser();
        }

        @Override
        protected void onPostExecute(FichaPersonal activeUser) {
            if (activeUser != null) {
                // Si hay un usuario activo, redirigir a la página de mesas con el usuario activo
                Intent intent = new Intent(MainActivity.this, MesasActivity.class);
                intent.putExtra("fichaPersonal", activeUser);
                intent.putExtra("seccionId", seccionId);
                startActivity(intent);
            } else {
                // Si no hay un usuario activo, ejecutar otra acción (redirigir a UsuariosActivity)
                new GetUsers().execute(seccionId);
            }
        }
    }
}
