package com.example.comandera.data;

import com.example.comandera.utils.Articulo;

import android.content.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ArticulosBD {

    private SQLServerConnection sqlServerConnection;

    public ArticulosBD(Context context) {
        sqlServerConnection = new SQLServerConnection(context);
    }

    public List<Articulo>  getArticulos(int familiaId, int zonaId) {
        List<Articulo> articulos = new ArrayList<>();

        if (sqlServerConnection.getConexion() != null) {
            try {
                String query = "SELECT * FROM ARTICULOS WHERE ESTADO = 0 AND VISIBLE_TPV = 1 AND FAMILIA_ID = ? AND ID NOT IN " +
                        "(SELECT ARTICULO_ID FROM ARTICULO_VETADO_ZONA WHERE FAMILIA_ID = ? AND ZONA_ID = ?)";
                PreparedStatement preparedStatement = sqlServerConnection.getConexion().prepareStatement(query);
                preparedStatement.setInt(1, familiaId);
                preparedStatement.setInt(2, familiaId);
                preparedStatement.setInt(3, zonaId);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    Articulo articulo = new Articulo();
                    articulo.setId(resultSet.getInt("ID"));
                    articulo.setCodigo(resultSet.getString("CODIGO"));
                    articulo.setFamilia_id(resultSet.getInt("FAMILIA_ID"));
                    articulo.setNombre(resultSet.getString("ARTICULO"));
                    articulo.setUrl(resultSet.getString("IMAGEN"));
                    articulo.setTipoIVAid(resultSet.getInt("tipo_iva_id"));
                    articulos.add(articulo);
                }

                resultSet.close();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return articulos;
    }
}
