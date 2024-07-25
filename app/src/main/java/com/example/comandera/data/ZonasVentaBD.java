package com.example.comandera.data;

import com.example.comandera.utils.ZonaVenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ZonasVentaBD {
    private SQLServerConnection sqlConnection;

    public ZonasVentaBD(SQLServerConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public List<ZonaVenta> getZonasBySeccionId(int seccionId) {
        List<ZonaVenta> zonas = new ArrayList<>();
        String query = "SELECT id, zona FROM zonas_ventas WHERE seccion_id = ?";

        try (Connection connection = sqlConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, seccionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String zona = resultSet.getString("zona");
                zonas.add(new ZonaVenta(id, zona));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return zonas;
    }
}
