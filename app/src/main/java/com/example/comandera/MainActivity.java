package com.example.comandera;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.controls.Control;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.comandera.data.DispositivosBD;
import com.example.comandera.data.MesasBD;
import com.example.comandera.data.PreciosBD;
import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.data.TipoIVABD;
import com.example.comandera.data.UsuariosBD;
import com.example.comandera.data.ZonasVentaBD;
import com.example.comandera.utils.DeviceInfo;
import com.example.comandera.utils.FichaPersonal;
import com.example.comandera.utils.ZonaVenta;


public class MainActivity extends AppCompatActivity {
    private VariablesGlobales varGlob;
    private ControlBloqueo screenReceiver;
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
        varGlob= (VariablesGlobales) getApplicationContext();

        // Obtener el ANDROID_ID
        varGlob.setMacActual(DeviceInfo.getAndroidID(this));
        new getMACS().execute();

        //Servicio para gestionar la salida forzosa
        Intent controlApagado = new Intent(this, ControlApagado.class);
        startService(controlApagado);

        // BroadcastReceiver para gestionar el bloqueo de pantalla
        screenReceiver = new ControlBloqueo();
        // Crear el IntentFilter para la acción de pantalla apagada
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        // Registrar el receptor con el filtro
        registerReceiver(screenReceiver, filter);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(screenReceiver);
    }
    //Arreglo para que si pulsas el boton de retroceso cierre la app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(1);
    }



    //Comprobar si la mac de nuestro dispositivio está en nuestra base de datos
    private class getMACS extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            varGlob.setConexionSQL(new SQLServerConnection(MainActivity.this));
            DispositivosBD dispositivosBD = new DispositivosBD(varGlob.getConexionSQL());
            return dispositivosBD.checkIfMacExists(varGlob.getMacActual());
        }
        @Override
        protected void onPostExecute(Boolean exists) {
            // Si la mac no esta en nuestra base de datos, tendremos que grabar la mac en la bbdd con la interfaz Config, si esta buscaremos los usuarios
            if (!exists) {
                Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
                startActivity(intent);
            } else {
                new GetSeccionyDispositivoIdTask().execute();
            }
        }
    }

    // cojo el id de la seccion que tenga nuestro dispositivo
    private class GetSeccionyDispositivoIdTask extends AsyncTask<String, Void, int[]> {
        @Override
        protected int[] doInBackground(String... params) {
            DispositivosBD dispositivosBD = new DispositivosBD(varGlob.getConexionSQL());
            return dispositivosBD.getIdSeccionYDispositivo(varGlob.getMacActual());
        }


        @Override
        protected void onPostExecute(int[] vector) {
            varGlob.setIdDispositivoActual(vector[0]);
            varGlob.setSeccionIdUsuariosActual(vector[1]);
            if (varGlob.getSeccionIdUsuariosActual()>0) {
                new GetActiveUserZonasMesasPrecios().execute();
            } else {
                Toast.makeText(MainActivity.this, "No se encontró la sección", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Obtener el usuario activo de la sección (RECORDAR USUARIO) y cargar la lista de usuarios en las variables globales y las zonas con sus mesas
    private class GetActiveUserZonasMesasPrecios extends AsyncTask<Integer, Void, FichaPersonal> {
        @Override
        protected FichaPersonal doInBackground(Integer... params) {
            UsuariosBD usuariosBD = new UsuariosBD(varGlob.getConexionSQL());
            ZonasVentaBD zonasVentaBD= new ZonasVentaBD(varGlob.getConexionSQL());
            MesasBD mesasBD=new MesasBD(varGlob.getConexionSQL());
            TipoIVABD tipoIVABD= new TipoIVABD(varGlob.getConexionSQL());
            PreciosBD preciosBD=new PreciosBD(varGlob.getConexionSQL());
            varGlob.setTarifasDeVentas(preciosBD.cargarTablaPrecios());
            varGlob.setTiposIVA(tipoIVABD.cargarTablaTiposDeIVA());
            varGlob.setListaZonas(zonasVentaBD.getZonasBySeccionId(varGlob.getSeccionIdUsuariosActual()));
            for (ZonaVenta item : varGlob.getListaZonas()) {
                item.setListaMesas(mesasBD.getMesasByZonaId(item.getId()));
            }
            varGlob.setListaUsuarios(usuariosBD.getUsers(varGlob.getSeccionIdUsuariosActual()));
            return usuariosBD.getActiveUser(varGlob.getMacActual());
        }

        @Override
        protected void onPostExecute(FichaPersonal activeUser) {

            if (activeUser != null) {
                // Si hay un usuario activo, redirigir a la página de mesas con el usuario activo
                Intent intent = new Intent(MainActivity.this, MesasActivity.class);
                varGlob.setUsuarioActual(activeUser);
                startActivity(intent);
            } else {
                // Si no hay un usuario activo, ejecutar otra acción (redirigir a UsuariosActivity o si no hay usuarios en esa seccion directamente a las mesas)
                if (!varGlob.getListaUsuarios().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, UsuariosActivity.class);
                    startActivity(intent);
                } else {
                    // Si no hay usuarios que salte directamente a la pagina de las mesas
                    Intent intent = new Intent(MainActivity.this, MesasActivity.class);
                    System.out.println(varGlob.getSeccionIdUsuariosActual());
                    startActivity(intent);
                }
            }
        }
    }
}
