package com.example.comandera;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comandera.utils.FichaPersonal;
//Esta es la prueba
public class ComensalesActivity extends AppCompatActivity {
    TextView tvZona;
    EditText comensales;
    int mesaId, seccionId;
    TextView tvUser;
    FichaPersonal fichaPersonal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comensales);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvZona = findViewById(R.id.zona);
        tvUser = findViewById(R.id.tvUser);
        comensales = findViewById(R.id.editTextComensales);
        fichaPersonal = getIntent().getParcelableExtra("fichaPersonal");
        mesaId = getIntent().getIntExtra("mesaId", -1);
        seccionId = getIntent().getIntExtra("seccionId", -1);


        if(fichaPersonal != null){
            tvUser.setText("Comandera/ " +fichaPersonal.getUsuarioApp());
        }

        mostrarTeclado();
        comensales.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        Intent i = new Intent(ComensalesActivity.this, FamiliasActivity.class);
                        int zonaId = getIntent().getIntExtra("zonaId", -1);
                        i.putExtra("fichaPersonal", fichaPersonal);
                        i.putExtra("zonaId", zonaId);
                        i.putExtra("mesaId", mesaId);
                        i.putExtra("seccionId", seccionId);
                    System.out.println("ca"+comensales);

                    i.putExtra("comensales", Integer.parseInt(comensales.getText().toString()));
                    startActivity(i);
                    return true;
                }
                return false;
            }
        });
    }
    private void mostrarTeclado(){
        // Mostrar el teclado automáticamente
        comensales.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(comensales, InputMethodManager.SHOW_IMPLICIT);

        String nombreMesa = getIntent().getStringExtra("mesaNombre");
        String zonaVenta = getIntent().getStringExtra("zonaVenta");

        if(nombreMesa != null){
            tvZona.setText(zonaVenta.toUpperCase()+" "+nombreMesa);
        }

    }
}