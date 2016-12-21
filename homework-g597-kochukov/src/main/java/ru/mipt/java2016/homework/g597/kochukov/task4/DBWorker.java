package ru.mipt.java2016.homework.g597.kochukov.task4;


import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;


/**
 * Created by tna0y on 20/12/16.
 */

public class DBWorker {


    public static class DBQuerryResult<T> {
        private T result;
        private int responseCode;

        public DBQuerryResult(T res, int code) {
            result = res;
            responseCode = code;
        }

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }

    }

    private static DBWorker instance = new DBWorker();

    private Connection conn;

    private DBWorker() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:calculator_meta.db");
            initializeTable();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public static DBWorker getInstance() {
        return instance;
    }

    private void initializeTable() {

        try {
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS USERS; DROP TABLE IF EXISTS VARIABLES; DROP TABLE IF EXISTS FUNCTIONS;" +
                    " CREATE TABLE USERS " +
                    "(ID INTEGER PRIMARY KEY    AUTOINCREMENT NOT NULL," +
                    " USERNAME       TEXT   NOT NULL, " +
                    " PASSWORD       TEXT   NOT NULL);" +
                    "CREATE TABLE VARIABLES " +
                    "(ID INTEGER PRIMARY KEY    AUTOINCREMENT NOT NULL," +
                    " OWNER          INTEGER    NOT NULL," +
                    " NAME           TEXT   NOT NULL," +
                    " VALUE          DOUBLE NOT NULL);" +
                    "CREATE TABLE FUNCTIONS " +
                    "(ID INTEGER PRIMARY KEY    AUTOINCREMENT NOT NULL," +
                    " OWNER          INTEGER    NOT NULL," +
                    " NAME           TEXT   NOT NULL," +
                    " EXPRESSION     TEXT   NOT NULL," +
                    " ARGC           INT    NOT NULL," +
                    " ARGV           TEXT   NOT NULL);";

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public DBQuerryResult<Double> getVariable(String name, Integer userid) {

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT VALUE FROM VARIABLES WHERE OWNER=" +
                            userid + " AND NAME='" + name + "' LIMIT 1; ");
            Double value;
            if (rs.next()) {
                value = rs.getDouble("VALUE");
            } else {
                return new DBQuerryResult<Double>(0.0, 404);
            }
            rs.close();
            stmt.close();
            return new DBQuerryResult<Double>(value, 200);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<Double>(0.0, 500);
        }
    }

    public DBQuerryResult<Double> setVariable(String name, Double value, Integer userid) {

        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO VARIABLES (OWNER,NAME,VALUE) " +
                    "VALUES (" + userid + ", '" + name + "', " + value + ");";
            stmt.executeUpdate(sql);
            stmt.close();
            return new DBQuerryResult<Double>(value, 200);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<Double>(0.0, 500);
        }

    }

    public DBQuerryResult<Double> deleteVariable(String name, Integer userid) {
        try {
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM VARIABLES WHERE NAME='" + name + "' AND OWNER='" + userid + "';";
            stmt.executeUpdate(sql);
            stmt.close();
            return new DBQuerryResult<Double>(0.0, 200);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<Double>(0.0, 500);
        }

    }

    public DBQuerryResult<LinkedHashMap<String, Double>> getUserScope(Integer userid) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT NAME, VALUE FROM VARIABLES WHERE OWNER=" + userid + ";");
            LinkedHashMap<String, Double> res = new LinkedHashMap<>();
            while (rs.next()) {
                try {
                    res.put(rs.getString("NAME"), rs.getDouble("VALUE"));
                } catch (SQLException e) {
                    System.err.println("Error fetching:" + e.getClass().getName() + ": " + e.getMessage());
                }
            }
            rs.close();
            stmt.close();
            return new DBQuerryResult<>(res, 200);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<>(null, 500);
        }

    }

    public DBQuerryResult<ArrayList<String>> listVariables(Integer userid) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT NAME FROM VARIABLES WHERE OWNER=" + userid + ";");
            ArrayList<String> names = new ArrayList<>();
            while (rs.next()) {
                names.add(rs.getString("NAME"));
            }
            rs.close();
            stmt.close();
            return new DBQuerryResult<ArrayList<String>>(names, 200);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<ArrayList<String>>(null, 500);
        }

    }

    public DBQuerryResult<Expression> getFunction(String name, Integer argc, Integer userid) {

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT EXPRESSION, ARGV FROM FUNCTIONS WHERE OWNER="
                                            + userid + " AND NAME='" + name + "' LIMIT 1;");
            String expr;
            LinkedHashMap<String, Double> hm = new LinkedHashMap<>();
            if (rs.next()) {
                expr = rs.getString("EXPRESSION");
                for (String key : rs.getString("ARGV").split(";")) {
                    hm.put(key, 0.0);
                }
            } else {
                return new DBQuerryResult<>(null, 404);
            }
            rs.close();
            stmt.close();
            Expression res = new Expression(expr, hm);
            res.setName(name);
            return new DBQuerryResult<>(res, 200);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<>(null, 500);
        }
    }

    public DBQuerryResult<Expression> getFunctionWithArguments(String name, Integer argc,
                                                               ArrayList<Double> argv, Integer userid) {
        DBQuerryResult<Expression> result = getFunction(name, argc, userid);
        int i = 0;
        Iterator<String> it = result.result.getScopeVars().keySet().iterator();
        LinkedHashMap<String, Double> tmp = new LinkedHashMap<>();
        while (it.hasNext()) {
            String key = it.next();
            tmp.put(key, argv.get(i++));
        }
        result.result.setScopeVars(tmp);

        return result;
    }

    public DBQuerryResult<Expression> setFunction(String name, String expression,
                                                  Integer argc, String argv, Integer userid) throws SQLException {

        if (Arrays.asList(DefaultCalculator.DEFAULTS).contains(name)) {
            throw new SQLException("Must not redefine existing functions.");
        }

        try {


            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO FUNCTIONS (OWNER,NAME,EXPRESSION,ARGC,ARGV) " +
                    "VALUES (" + userid + ", '" + name + "', '" + expression + "', " + argc + ",'" + argv + "');";
            stmt.executeUpdate(sql);
            stmt.close();
            LinkedHashMap<String, Double> hm = new LinkedHashMap<>();
            for (String key : argv.split(";")) {
                hm.put(key, 0.0);
            }
            return new DBQuerryResult<>(new Expression(expression, hm), 200);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<>(null, 500);
        }

    }

    public DBQuerryResult<Expression> deleteFunction(String name, Integer argc, Integer userid) {
        try {
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM FUNCTIONS WHERE NAME='" + name + "' AND OWNER='" + userid + "';";
            stmt.executeUpdate(sql);
            stmt.close();
            return new DBQuerryResult<>(null, 200);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<>(null, 500);
        }
    }


    public DBQuerryResult<ArrayList<Expression>> listFunctions(Integer userid) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT NAME, EXPRESSION, ARGV VALUE FROM FUNCTIONS WHERE OWNER="
                                            + userid + ";");
            ArrayList<Expression> res = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("NAME");
                String expr = rs.getString("EXPRESSION");
                String argv = rs.getString("ARGV");

                LinkedHashMap<String, Double> hm = new LinkedHashMap<>();
                for (String key : argv.split(";")) {
                    hm.put(key, 0.0);
                }
                Expression e = new Expression(expr, hm);
                e.setName(name);
                res.add(e);
            }
            rs.close();
            stmt.close();
            return new DBQuerryResult<ArrayList<Expression>>(res, 200);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<ArrayList<Expression>>(null, 500);
        }
    }

    public DBQuerryResult<Integer> authenticate(String login, String password) {
        // System.out.println("Auth: "+login+" "+password);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ID FROM USERS WHERE USERNAME='"
                                            + login + "' AND PASSWORD='" + password + "';");
            Integer uid;
            if (rs.next()) {
                uid = rs.getInt("ID");
            } else {
                return new DBQuerryResult<Integer>(-1, 401); // 401 - unauthorized
            }
            rs.close();
            stmt.close();
            return new DBQuerryResult<Integer>(uid, 200);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<Integer>(-1, 500);
        }
    }


    public DBQuerryResult<Integer> register(String login, String password) {

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM USERS WHERE USERNAME='" + login + "';");
            if (rs.next()) {
                return new DBQuerryResult<Integer>(-1, 401); // 401 - unauthorized
            }
            rs.close();
            stmt.close();

            stmt = conn.createStatement();
            String sql = "INSERT INTO USERS (USERNAME,PASSWORD) VALUES ('" + login + "', '" + password + "');";
            stmt.executeUpdate(sql);
            stmt.close();
            return authenticate(login, password);
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return new DBQuerryResult<Integer>(-1, 500);
        }

    }

    public BillingUser getUser(String username) {
        // System.out.println("Getting user: " + username);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ID, USERNAME, PASSWORD FROM USERS WHERE USERNAME='"
                                            + username + "';");
            Integer uid;
            String u;
            String p;
            if (rs.next()) {
                uid = rs.getInt("ID");
                u = rs.getString("USERNAME");
                p = rs.getString("PASSWORD");
                rs.close();
                stmt.close();
                return new BillingUser(u, p, true, uid);
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

}