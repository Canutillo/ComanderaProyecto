package com.example.comandera.data;

import android.database.SQLException;

import com.example.comandera.utils.TarifasDeVenta;
import com.example.comandera.utils.TipoIVA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PreciosBD {

    private SQLServerConnection sqlConnection;

    public PreciosBD(SQLServerConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public List<TarifasDeVenta> cargarTablaPrecios(){
        List <TarifasDeVenta> tarifasDeVentas= new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            if (sqlConnection.getConexion() != null) {
                String query = "SELECT id, precio_venta, tipo_tarifa_id,articulo_id FROM TARIFA_VENTA";
                statement = sqlConnection.getConexion().prepareStatement(query);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    TarifasDeVenta tarifasDeVenta=new TarifasDeVenta();
                    tarifasDeVenta.setId(resultSet.getInt("id"));
                    tarifasDeVenta.setPrecio(resultSet.getDouble("precio_venta"));
                    tarifasDeVenta.setTipoTarifa(resultSet.getInt("tipo_tarifa_id"));
                    tarifasDeVenta.setArticuloId(resultSet.getInt("articulo_id"));
                    tarifasDeVentas.add(tarifasDeVenta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException | java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return tarifasDeVentas;
    }
}
