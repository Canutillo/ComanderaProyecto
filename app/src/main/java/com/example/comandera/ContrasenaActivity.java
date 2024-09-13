package com.example.comandera;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comandera.data.UsuariosBD;

public class ContrasenaActivity extends AppCompatActivity {
    private VariablesGlobales varGlob;

    TextView nombreUser;
    EditText contrasena;
    CheckBox checkActivo;
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
        varGlob=(VariablesGlobales) getApplicationContext();

        nombreUser = findViewById(R.id.nombreUser);
        contrasena = findViewById(R.id.editTextContrasena);
        checkActivo = findViewById(R.id.checkActivo);

        mostrarTeclado();
        nombreUser.setText(varGlob.getUsuarioActual().getUsuarioApp());

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
        Intent intent = new Intent(this, UsuariosActivity.class);
        startActivity(intent);
        finish();  // Si quieres cerrar la actividad actual
    }

    private void verificarContrasena() {
        if (varGlob.getUsuarioActual() != null) {
            String contrasenaIntroducida = contrasena.getText().toString().trim();
            String contrasenaAlmacenada = varGlob.getUsuarioActual().getContrasenaApp();

            if (contrasenaIntroducida.equals(contrasenaAlmacenada)) {
                // Contraseña correcta, si el checkbox esta seleccionado activa el usuario en la bbdd
                if (checkActivo.isChecked()) {
                    new SetActiveUserTask().execute();
                } else {
                    Intent i = new Intent(ContrasenaActivity.this, MesasActivity.class);
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
            UsuariosBD usuariosBD = new UsuariosBD(varGlob.getConexionSQL());
            return usuariosBD.setActiveUser(varGlob.getUsuarioActual().getId(), varGlob.getMacActual());
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(ContrasenaActivity.this, "Usuario activado correctamente", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ContrasenaActivity.this, MesasActivity.class);
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
    }
}