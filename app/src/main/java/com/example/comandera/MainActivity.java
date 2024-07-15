package com.example.comandera;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comandera.data.ApiService;
import com.example.comandera.utils.User;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = new ApiService();
        new FetchUsuariosTask().execute();
    }

    private class FetchUsuariosTask extends AsyncTask<Void, Void, List<User>> {
        private IOException exception;

        @Override 
        protected List<User> doInBackground(Void... voids) {
            try {
                return apiService.fetchUsuarios();
            } catch (IOException e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<User> usuarios) {
            if (exception != null) {
                Log.e(TAG, "Error al obtener los usuarios", exception);
                // Manejar error, mostrar mensaje al usuario
            } else {
                for (User user : usuarios) {
                    Log.d(TAG, "Usuario: " + user.getTrabajador() + ", DNI: " + user.getDNI());
                }
                // Actualizar UI con los datos de usuarios
            }
        }
    }
}
