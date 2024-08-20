package com.example.comandera.data;

import android.database.SQLException;

import com.example.comandera.utils.PreguntaArticulo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PreguntasBD {
    private SQLServerConnection sqlConnection;

    public PreguntasBD(SQLServerConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    // Obtener preguntas ordenadas por la columna 'orden'
    public List<PreguntaArticulo> getPreguntas(int articuloId) {
        List<PreguntaArticulo> preguntasList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = sqlConnection.connect();
            if (connection != null) {
                String query = "SELECT id, texto FROM Preguntas_Articulo WHERE articulo_id = ? ORDER BY orden ASC";
                statement = connection.prepareStatement(query);
                statement.setInt(1, articuloId);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String texto = resultSet.getString("texto");
                    preguntasList.add(new PreguntaArticulo(id, texto));
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
                if (connection != null) connection.close();
            } catch (SQLException | java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return preguntasList;
    }

    // Obtener opciones basadas en pregunta_articulo_id
    public List<String> getOpciones(int preguntaId) {
        List<String> opcionesList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = sqlConnection.connect();
            if (connection != null) {
                String query = "SELECT articulo FROM Articulos WHERE id IN (SELECT articulo_id FROM Opciones_Preguntas_Articulo WHERE pregunta_articulo_id = ?)";
                statement = connection.prepareStatement(query);
                statement.setInt(1, preguntaId);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String opcion = resultSet.getString("articulo");
                    opcionesList.add(opcion);
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
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (java.sql.SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return opcionesList;
    }
}
