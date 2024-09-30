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
import com.example.comandera.utils.Ticket;

public class ComensalesActivity extends AppCompatActivity {
    VariablesGlobales varGlob;
    TextView tvZona;
    EditText comensales;
    TextView tvUser;
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
        varGlob=(VariablesGlobales) getApplicationContext();

        tvZona = findViewById(R.id.zona);
        tvUser = findViewById(R.id.tvUser);
        comensales = findViewById(R.id.editTextComensales);


        if(varGlob.getUsuarioActual() != null){
            tvUser.setText("Comandera/ " +varGlob.getUsuarioActual().getUsuarioApp());
        }
        mostrarTeclado();

        comensales.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    Intent i = new Intent(ComensalesActivity.this, FamiliasActivity.class);
                    Ticket ticket=new Ticket();
                    ticket.setComensales(Integer.parseInt(comensales.getText().toString()));
                    ticket.setNuevo(true);
                    varGlob.setTicketActual(ticket);
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
        if(varGlob.getMesaActual() != null){
            tvZona.setText(varGlob.getZonaActual().getZona()+" "+varGlob.getMesaActual().getNombre());
        }

    }
}