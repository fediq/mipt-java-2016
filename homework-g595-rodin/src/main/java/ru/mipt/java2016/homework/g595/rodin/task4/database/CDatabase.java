package ru.mipt.java2016.homework.g595.rodin.task4.database;


import java.io.Closeable;
import java.sql.*;


public class CDatabase implements Closeable {


    private Connection connection;

    private Statement statement;

    private ResultSet resultSet;

    private int userID = -1;

    public CDatabase() {
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
            close();
        }

    }


    private void connect() throws SQLException {

        try {
            String driverName = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/calculator_utils";
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, "root", "root");
            statement = connection.createStatement();
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void close() {
        try {
            connection.close();
            statement.close();
            resultSet.close();
        } catch (Exception exception) {
            return;
        }
    }

}