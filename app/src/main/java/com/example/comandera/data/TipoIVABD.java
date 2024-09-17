package com.example.comandera.data;

import android.database.SQLException;

import com.example.comandera.utils.PreguntaArticulo;
import com.example.comandera.utils.TipoIVA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TipoIVABD {

    private SQLServerConnection sqlConnection;

    public TipoIVABD(SQLServerConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public List<TipoIVA> cargarTablaTiposDeIVA(){
        List <TipoIVA> tiposDeIVA= new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            if (sqlConnection.getConexion() != null) {
                String query = "SELECT id, tipo_iva FROM TIPOS_IVA";
                statement = sqlConnection.getConexion().prepareStatement(query);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    TipoIVA tipo=new TipoIVA();
                    tipo.setId(resultSet.getInt("id"));
                    tipo.setValorIVA(resultSet.getDouble("tipo_iva"));
                    tiposDeIVA.add(tipo);
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
        return tiposDeIVA;
    }
}
