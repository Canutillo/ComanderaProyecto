package com.example.comandera.data;

import com.example.comandera.utils.Dispositivo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DispositivosBD {
    private SQLServerConnection sqlConnection;

    public DispositivosBD(SQLServerConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public List<Dispositivo> getDispositivos() {
        List<Dispositivo> dispositivos = new ArrayList<>();
        try {
            if (sqlConnection.getConexion() != null) {
                String query = "SELECT descripcion, id FROM Dispositivos WHERE mac IS NULL";
                PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String descripcion = resultSet.getString("descripcion");
                    int id = resultSet.getInt("id");
                    dispositivos.add(new Dispositivo(id, descripcion));
                }
                resultSet.close();
                preparedStatement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dispositivos;
    }

    public boolean updateMac(int id, String mac) {
        boolean success = false;
        try {
            if (sqlConnection.getConexion() != null) {
                String query = "UPDATE Dispositivos SET mac = ? WHERE id = ?";
                PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                preparedStatement.setString(1, mac);
                preparedStatement.setInt(2, id);
                int rowsUpdated = preparedStatement.executeUpdate();
                success = (rowsUpdated > 0);

                preparedStatement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean checkIfMacExists(String mac) {
        boolean exists = false;
        try {
            if (sqlConnection.getConexion() != null) {
                String query = "SELECT COUNT(*) FROM Dispositivos WHERE mac = ?";
                PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                preparedStatement.setString(1, mac);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    exists = (count > 0);
                }

                resultSet.close();
                preparedStatement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    public int[] getIdSeccionYDispositivo(String mac) {
        int[] vector = new int[2];

        try {
            if (sqlConnection.getConexion() != null) {
                String query = "SELECT id,seccion_id FROM Dispositivos WHERE mac = ?";
                PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                preparedStatement.setString(1, mac);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    vector[0] = resultSet.getInt("id");
                    vector[1] = resultSet.getInt("seccion_id");
                }

                resultSet.close();
                preparedStatement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vector;
    }

    public int getId(String mac) {
        Integer id = null;

        try {
            if (sqlConnection.getConexion() != null) {
                String query = "SELECT id FROM Dispositivos WHERE mac = ?";
                PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                preparedStatement.setString(1, mac);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    id = resultSet.getInt("id");
                }

                resultSet.close();
                preparedStatement.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}
