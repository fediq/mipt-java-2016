package ru.mipt.java2016.homework.g595.rodin.task4.database;


import java.io.Closeable;
import java.sql.*;
import java.util.ArrayList;


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


    public boolean addVariable(String name, String type, String text) {
        String query = "INSERT INTO calc_variables(var_name, type, value) VALUES (?,?,?);";

        try {
            if (checkVariable(name)) {
                return false;
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, text);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean checkVariable(String name) {
        String query = "SELECT id FROM calc_variables WHERE var_name = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean addFunction(String name, int argumentsNumber, String prototype) {
        String query = "INSERT INTO calc_functions(func_name, arg_num, prototype) VALUES (?, ?, ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, argumentsNumber);
            preparedStatement.setString(3, prototype);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addFunctionArgument(int functionId, String argType, int argPos) {
        String query  = "INSERT INTO calc_arguments(func_id, arg_type, arg_pos) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, functionId);
            preparedStatement.setString(2, argType);
            preparedStatement.setInt(3, argPos);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public int getFunctionId(String prototype) {
        String query = "SELECT id FROM calc_functions WHERE prototype = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, prototype);
            resultSet = preparedStatement.executeQuery();
            int id = -1;
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            return id;
        } catch (SQLException e) {
            return -1;
        }
    }


    public ArrayList<CVariablePackage> getAllVariables() {
        String query = "SELECT var_name, type, value FROM calc_variables;";
        try {
            resultSet = statement.executeQuery(query);
            ArrayList<CVariablePackage> result = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("var_name");
                String type = resultSet.getString("type");
                String value = resultSet.getString("value");
                result.add(new CVariablePackage(name, type, value));
            }
            return result;
        } catch (SQLException e) {
            return null;
        }
    }

    public CVariablePackage getVariable(String name) {
        String query = "SELECT var_name, type, value FROM calc_variables WHERE var_name = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String varName = resultSet.getString("var_name");
                String value = resultSet.getString("value");
                String type = resultSet.getString("type");
                return new CVariablePackage(name, type, value);
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean deleteVariable(String name) {
        String query = "DELETE FROM calc_variables WHERE var_name = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            return false;
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