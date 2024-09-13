package com.example.comandera.data;

import com.example.comandera.utils.FichaPersonal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class UsuariosBD {
    private SQLServerConnection sqlConnection;

    public UsuariosBD(SQLServerConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }
    public List<FichaPersonal> getUsers(int seccionId) {
        List<FichaPersonal> fichas = new ArrayList<>();
        try {
            if (sqlConnection.getConexion() != null) {
                String query = "SELECT id, usuario_app, contrasena_app, seccion_id_1, seccion_id_2, seccion_id_3, acceso_tpv, estado " +
                        "FROM Ficha_Personal WHERE estado = 0 AND acceso_tpv = 'true' AND (seccion_id_1 = ? OR seccion_id_2 = ? OR seccion_id_3 = ?)";
                PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                preparedStatement.setInt(1, seccionId);
                preparedStatement.setInt(2, seccionId);
                preparedStatement.setInt(3, seccionId);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String usuarioApp = resultSet.getString("usuario_app");
                    String contrasenaApp = resultSet.getString("contrasena_app");
                    int seccionId1 = resultSet.getInt("seccion_id_1");
                    int seccionId2 = resultSet.getInt("seccion_id_2");
                    int seccionId3 = resultSet.getInt("seccion_id_3");
                    boolean accesoTpv = resultSet.getBoolean("acceso_tpv");
                    int estado = resultSet.getInt("estado");
                    FichaPersonal ficha = new FichaPersonal(id, usuarioApp, contrasenaApp, seccionId1, seccionId2, seccionId3, accesoTpv, estado);
                    fichas.add(ficha);
                }

                resultSet.close();
                preparedStatement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fichas;
    }

    public boolean existsContrasena() {
        boolean exists = false;
        try {
            if (sqlConnection.getConexion() != null) {
                String query = "SELECT TOP 1 1 FROM Ficha_Personal WHERE contrasena_app IS NOT NULL AND acceso_tpv = 1 AND estado = 0";
                PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    exists = true;
                }

                resultSet.close();
                preparedStatement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    public FichaPersonal getActiveUser(String macAddress) {
        FichaPersonal activeUser = null;
        int usuarioId = -1;
        PreparedStatement getDeviceIdStmt = null;
        ResultSet deviceIdResultSet = null;
        PreparedStatement macCheckStmt = null;
        ResultSet macCheckResult = null;
        PreparedStatement userStmt = null;
        ResultSet userResult = null;

        try {
            if (sqlConnection.getConexion() != null) {
                String getDeviceIdQuery = "SELECT id FROM dispositivos WHERE mac = ?";
                getDeviceIdStmt = sqlConnection.getConexion().prepareStatement(getDeviceIdQuery);
                getDeviceIdStmt.setString(1, macAddress);
                deviceIdResultSet = getDeviceIdStmt.executeQuery();

                int deviceId = -1;
                if (deviceIdResultSet.next()) {
                    deviceId = deviceIdResultSet.getInt("id");
                }

                if (deviceId != -1) {
                    // Verificar si la MAC tiene un usuario asociado
                    String macCheckQuery = "SELECT id_usuario FROM Dispositivos_usuarios WHERE id_dispositivo = ?";
                    macCheckStmt = sqlConnection.getConexion().prepareStatement(macCheckQuery);
                    macCheckStmt.setInt(1, deviceId);
                    macCheckResult = macCheckStmt.executeQuery();

                    if (macCheckResult.next()) {
                        // La MAC está asociada con el usuario
                        usuarioId = macCheckResult.getInt("id_usuario");
                        String userQuery = "SELECT id, usuario_app, contrasena_app, seccion_id_1, seccion_id_2, seccion_id_3, acceso_tpv, estado " +
                                "FROM Ficha_Personal WHERE id = ? AND estado = 0 AND acceso_tpv = 1";
                        userStmt = sqlConnection.getConexion().prepareStatement(userQuery);
                        userStmt.setInt(1, usuarioId);
                        userResult = userStmt.executeQuery();

                        if (userResult.next()) {
                            int id = userResult.getInt("id");
                            String usuarioApp = userResult.getString("usuario_app");
                            String contrasenaApp = userResult.getString("contrasena_app");
                            int seccionId1 = userResult.getInt("seccion_id_1");
                            int seccionId2 = userResult.getInt("seccion_id_2");
                            int seccionId3 = userResult.getInt("seccion_id_3");
                            boolean accesoTpv = userResult.getBoolean("acceso_tpv");
                            int estado = userResult.getInt("estado");
                            activeUser = new FichaPersonal(id, usuarioApp, contrasenaApp, seccionId1, seccionId2, seccionId3, accesoTpv, estado);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (userResult != null) userResult.close();
                if (userStmt != null) userStmt.close();
                if (macCheckResult != null) macCheckResult.close();
                if (macCheckStmt != null) macCheckStmt.close();
                if (deviceIdResultSet != null) deviceIdResultSet.close();
                if (getDeviceIdStmt != null) getDeviceIdStmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return activeUser;
    }


    public boolean setActiveUser(int userId, String macAddress) {
        boolean success = false;
        try {
            if (sqlConnection.getConexion() != null) {
                String getDeviceIdQuery = "SELECT id FROM dispositivos WHERE mac = ?";
                PreparedStatement getDeviceIdStmt = sqlConnection.getConexion().prepareStatement(getDeviceIdQuery);
                getDeviceIdStmt.setString(1, macAddress);
                ResultSet deviceIdResultSet = getDeviceIdStmt.executeQuery();

                int deviceId = -1;
                if (deviceIdResultSet.next()) {
                    deviceId = deviceIdResultSet.getInt("id");
                }

                if (deviceId != -1) {
                    String query = "INSERT INTO Dispositivos_usuarios (id_dispositivo, id_usuario) VALUES (?, ?)";
                    PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                    preparedStatement.setInt(1, deviceId);
                    preparedStatement.setInt(2, userId);

                    int rowsAffected = preparedStatement.executeUpdate();
                    success = rowsAffected > 0;

                    preparedStatement.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }


    public void unsetActiveUser(int userId, String mac){
        try {
            if (sqlConnection.getConexion() != null) {
                String getDeviceIdQuery = "SELECT id FROM dispositivos WHERE mac = ?";
                PreparedStatement getDeviceIdStmt = sqlConnection.getConexion().prepareStatement(getDeviceIdQuery);
                getDeviceIdStmt.setString(1, mac);
                ResultSet deviceIdResultSet = getDeviceIdStmt.executeQuery();

                int deviceId = -1;
                if (deviceIdResultSet.next()) {
                    deviceId = deviceIdResultSet.getInt("id");
                }else{
                    System.out.println("ID dispositivo no encontrado");
                }

                if (deviceId != -1) {
                    String query = "DELETE FROM Dispositivos_Usuarios WHERE id_usuario = ? AND id_dispositivo = ?;";
                    PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                    preparedStatement.setInt(1, userId);
                    preparedStatement.setInt(2, deviceId);
                    // Ejecutar la actualización
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        // Actualización exitosa
                        System.out.println("Sesion cerrada.");
                    } else {
                        // No se encontró ninguna fila para actualizar
                        System.out.println("No habia sesion iniciada.");
                    }

                    preparedStatement.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quitarMac(String mac) {
        try {
            if (sqlConnection.getConexion() != null) {
                String query = "UPDATE Dispositivos SET mac = NULL WHERE mac = ?;";
                PreparedStatement preparedStatement = sqlConnection.getConexion().prepareStatement(query);
                preparedStatement.setString(1, mac);
                // Ejecutar la actualización
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    // Actualización exitosa
                    System.out.println("Mac desvinculada correctamente.");
                } else {
                    // No se encontró ninguna fila para actualizar
                    System.out.println("No se encontró ningún dispositivo con esa MAC.");
                }
                preparedStatement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
