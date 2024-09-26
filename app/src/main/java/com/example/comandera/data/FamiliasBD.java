package com.example.comandera.data;

import android.content.Context;

import com.example.comandera.utils.Familia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FamiliasBD {
    private SQLServerConnection sqlServerConnection;

    public FamiliasBD(SQLServerConnection connection) {
        sqlServerConnection = connection;
    }

    public List<Familia> getVisibleFamilias(int zonaId) {
        List<Familia> familias = new ArrayList<>();
        String sql = "SELECT * FROM FAMILIAS WHERE VISIBLE_TPV = 1 AND ESTADO = 0 AND ID NOT IN " +
                "(SELECT familia_id FROM FAMILIA_VETADA_ZONA WHERE ZONA_ID = ?)";

        try {
            PreparedStatement statement = sqlServerConnection.getConexion().prepareStatement(sql);
            statement.setInt(1, zonaId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Familia familia = new Familia();
                familia.setId(resultSet.getInt("id"));
                familia.setNombre(resultSet.getString("codigo"));
                familia.setImage(resultSet.getString("imagen"));
                familias.add(familia);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return familias;
    }
}
