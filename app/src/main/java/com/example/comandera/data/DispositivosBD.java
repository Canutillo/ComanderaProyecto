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
            Connection connection = sqlConnection.connect();
            if (connection != null) {
                String query = "SELECT descripcion, id FROM Dispositivos WHERE mac IS NULL";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String descripcion = resultSet.getString("descripcion");
                    int id = resultSet.getInt("id");
                    dispositivos.add(new Dispositivo(id, descripcion));
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dispositivos;
    }

    public boolean updateMac(int id, String mac) {
        boolean success = false;
        try {
            Connection connection = sqlConnection.connect();
            if (connection != null) {
                String query = "UPDATE Dispositivos SET mac = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, mac);
                preparedStatement.setInt(2, id);
                int rowsUpdated = preparedStatement.executeUpdate();
                success = (rowsUpdated > 0);

                preparedStatement.close();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean checkIfMacExists(String mac) {
        boolean exists = false;
        try {
            Connection connection = sqlConnection.connect();
            if (connection != null) {
                String query = "SELECT COUNT(*) FROM Dispositivos WHERE mac = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, mac);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    exists = (count > 0);
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    public int getIdSeccion(String mac) {
        Integer seccionId = null;

        try {
            Connection connection = sqlConnection.connect();
            if (connection != null) {
                String query = "SELECT seccion_id FROM Dispositivos WHERE mac = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, mac);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    seccionId = resultSet.getInt("seccion_id");
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seccionId;
    }

    public int getId(String mac) {
        Integer id = null;

        try {
            Connection connection = sqlConnection.connect();
            if (connection != null) {
                String query = "SELECT id FROM Dispositivos WHERE mac = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, mac);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    id = resultSet.getInt("id");
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}
