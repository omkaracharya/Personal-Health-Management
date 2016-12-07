package com.ncsu.dbms.project.healthsystem.data;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by watve on 10/13/2016.
 */
public class DatabaseConnection {
    private static final String jdbcURL = "jdbc:oracle:thin:@orca.csc.ncsu.edu:1521:orcl01";
    private Connection connection = null;

    public DatabaseConnection() {
        createConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    private void createConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String user = "oachary";
            String passwd = "200155609";
            connection = DriverManager.getConnection(jdbcURL, user, passwd);
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Throwable whatever) {
            }
        }
    }
}
