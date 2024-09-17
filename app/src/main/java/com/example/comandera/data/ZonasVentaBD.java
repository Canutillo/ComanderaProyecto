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

        try (PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query)) {

            preparedStatement.setInt(1, seccionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String zona = resultSet.getString("zona");
                int idTarifa=0;
                String queryTarifa="SELECT id FROM TIPOS_TARIFA_VENTA WHERE zona_id= ?";
                PreparedStatement preparedStatement1 =sqlConnection.getConexion().prepareStatement(queryTarifa);
                preparedStatement1.setInt(1,id);
                ResultSet resultSet1=preparedStatement1.executeQuery();
                if(resultSet1.next()){
                    idTarifa = resultSet1.getInt("id");
                }
                zonas.add(new ZonaVenta(id, zona, idTarifa));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return zonas;
    }
}
