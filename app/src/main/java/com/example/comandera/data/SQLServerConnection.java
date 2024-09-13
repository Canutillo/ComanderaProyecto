package com.example.comandera.data;

import android.content.Context;
import android.widget.Toast;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLServerConnection {
    private Context context;
    private Connection conexion;

    public SQLServerConnection(Context context) {

        this.context = context;
        this.connect();
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = context.getAssets().open("config.properties");
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public void connect() {
        this.conexion = null;
        try {
            Properties properties = loadProperties();
            String ip = properties.getProperty("ip");
            String port = properties.getProperty("port");
            String database = properties.getProperty("database");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + database + ";user=" + username + ";password=" + password + ";";
            this.conexion = DriverManager.getConnection(connectionUrl);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context.getApplicationContext(), "Ha fallado la conexion",Toast.LENGTH_SHORT);
        }
    }

    public void disconnect(){
        try {
            this.conexion.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Connection getConexion() {
        return conexion;
    }
}
