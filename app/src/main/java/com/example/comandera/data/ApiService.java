package com.example.comandera.data;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.example.comandera.utils.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ApiService {
    private final OkHttpClient client;
    private final Gson gson;

    public ApiService() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    // Método genérico para hacer solicitudes HTTP GET
    private String makeGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    // Método para obtener usuarios
    public List<User> fetchUsuarios() throws IOException {
        String url = "http://10.0.2.2:3000/api/usuarios";
        String responseData = makeGetRequest(url);
        System.out.println("Response Data: " + responseData);
        Type userListType = new TypeToken<List<User>>(){}.getType();
        return gson.fromJson(responseData, userListType);
    }

}