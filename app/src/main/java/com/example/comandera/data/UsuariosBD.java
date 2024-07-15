package com.example.comandera.data;

import com.example.comandera.MainActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class UsuariosBD {
    private SQLServerConnection sqlConnection;

    public UsuariosBD(SQLServerConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }
    public void executeQuery() {
        try {
            Connection connection = sqlConnection.connect();
            if (connection != null) {
                String query = "SELECT id, usuario_app, contraseña_app, seccion_id_1, seccion_id_2, seccion_id_3, acceso_tpv, estado \n" +
                        "                        FROM Ficha_Personal WHERE estado = 0 AND acceso_tpv = 'true'";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("USUARIO_APP");
                    String password = resultSet.getString("CONTRASEÑA_APP");
                    int seccion_id_1 = resultSet.getInt("SECCION_ID_1");
                    int seccion_id_2 = resultSet.getInt("SECCION_ID_2");
                    int seccion_id_3 = resultSet.getInt("SECCION_ID_3");
                    Boolean acceso_TPV = resultSet.getBoolean("ACCESO_TPV");
                    int estado = resultSet.getInt("ESTADO");

                    String resultString = "ID: " + id + "\n" +
                            "Username: " + username + "\n" +
                            "Password: " + password + "\n" +
                            "Sección ID 1: " + seccion_id_1 + "\n" +
                            "Sección ID 2: " + seccion_id_2 + "\n" +
                            "Sección ID 3: " + seccion_id_3 + "\n" +
                            "Acceso TPV: " + acceso_TPV + "\n" +
                            "Estado: " + estado + "\n";

                    System.out.println(resultString);
                }

                resultSet.close();
                statement.close();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
