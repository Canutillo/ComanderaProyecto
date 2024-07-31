package com.example.comandera.data;

import android.content.Context;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class SQLServerConnection {
    private Context context;

    public SQLServerConnection(Context context) {
        this.context = context;
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

    public Connection connect() {
        Connection connection = null;
        try {
            Properties properties = loadProperties();
            String ip = properties.getProperty("ip");
            String port = properties.getProperty("port");
            String database = properties.getProperty("database");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + database + ";user=" + username + ";password=" + password + ";";
            connection = DriverManager.getConnection(connectionUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
