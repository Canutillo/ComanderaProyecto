package com.example.comandera;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comandera.data.SQLServerConnection;
import com.example.comandera.data.UsuariosBD;
import com.example.comandera.utils.DeviceInfo;
import com.example.comandera.utils.FichaPersonal;

public class ContrasenaActivity extends AppCompatActivity {
    private FichaPersonal fichaPersonal;
    TextView nombreUser;
    EditText contrasena;
    CheckBox checkActivo;
    int seccionId;
    String androidID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contrasena);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nombreUser = findViewById(R.id.nombreUser);
        contrasena = findViewById(R.id.editTextContrasena);
        checkActivo = findViewById(R.id.checkActivo);

        seccionId = getIntent().getIntExtra("seccionId", -1);
        androidID = DeviceInfo.getAndroidID(this);


        mostrarTeclado();

        contrasena.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    verificarContrasena();
                    return true;
                }
                return false;
            }
        });
    }

    //Arreglo para que cuando intentes ir hacia atras vaya a usuarios empezando la app de nuevo
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();  // Si quieres cerrar la actividad actual
    }

    private void verificarContrasena() {
        if (fichaPersonal != null) {
            String contrasenaIntroducida = contrasena.getText().toString().trim();
            String contrasenaAlmacenada = fichaPersonal.getContrasenaApp();

            if (contrasenaIntroducida.equals(contrasenaAlmacenada)) {
                // Contraseña correcta, si el checkbox esta seleccionado activa el usuario en la bbdd
                if (checkActivo.isChecked()) {
                    new SetActiveUserTask().execute(fichaPersonal.getId());
                } else {
                    Intent i = new Intent(ContrasenaActivity.this, MesasActivity.class);
                    i.putExtra("fichaPersonal", fichaPersonal);
                    i.putExtra("seccionId", seccionId);
                    startActivity(i);
                }
            } else {
                // Contraseña incorrecta
                Toast.makeText(ContrasenaActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Tarea para actualizar el usuario activo
    private class SetActiveUserTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            int userId = params[0];
            SQLServerConnection sqlServerConnection = new SQLServerConnection(ContrasenaActivity.this);
            UsuariosBD usuariosBD = new UsuariosBD(sqlServerConnection);
            return usuariosBD.setActiveUser(userId, androidID);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(ContrasenaActivity.this, "Usuario activado correctamente", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ContrasenaActivity.this, MesasActivity.class);
                i.putExtra("fichaPersonal", fichaPersonal);
                i.putExtra("seccionId", seccionId);
                startActivity(i);
            } else {
                Toast.makeText(ContrasenaActivity.this, "Error al activar el usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mostrarTeclado(){
        // Mostrar el teclado automáticamente
        contrasena.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(contrasena, InputMethodManager.SHOW_IMPLICIT);

        fichaPersonal = getIntent().getParcelableExtra("fichaPersonal");

        if(fichaPersonal!= null){
            nombreUser.setText(fichaPersonal.getUsuarioApp());
        }
    }
}