package com.example.comandera.data;

import com.example.comandera.utils.Mesa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MesasBD {
    private SQLServerConnection sqlConnection;

    public MesasBD(SQLServerConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public List<Mesa> getMesasByZonaId(int zonaId) {
        List<Mesa> mesas = new ArrayList<>();
        try {
            if (sqlConnection.getConexion() != null) {
                String query = "SELECT id, zona_id, numero, nombre FROM Mesas WHERE zona_id = ?";
                PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                preparedStatement.setInt(1, zonaId);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int numero = resultSet.getInt("numero");
                    String nombre = resultSet.getString("nombre");
                    int zona_id = resultSet.getInt("zona_id");
                    Mesa mesa = new Mesa(id, numero, nombre, zona_id);
                    mesas.add(mesa);
                }
                resultSet.close();
                preparedStatement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mesas;
    }
}
