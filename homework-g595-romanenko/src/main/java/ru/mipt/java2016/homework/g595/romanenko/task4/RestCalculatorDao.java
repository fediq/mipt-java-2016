package ru.mipt.java2016.homework.g595.romanenko.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.romanenko.task4.calculator.Function;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ru.mipt.java2016.homework.g595.romanenko
 *
 * @author Ilya I. Romanenko
 * @since 16.12.16
 **/
@Repository
public class RestCalculatorDao {
    private static final Logger LOG = LoggerFactory.getLogger(RestCalculatorDao.class);

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private Connection connection;

    private PreparedStatement insertUser;
    private PreparedStatement insertVariable;
    private PreparedStatement insertFunction;
    private PreparedStatement deleteFunction;
    private PreparedStatement deleteVariable;

    @PostConstruct
    public void postConstruct() {
        jdbcTemplate = new JdbcTemplate(dataSource, false);
        connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Can't access connection");
        }
        initSchema();
        prepareStatements();
    }

    private void prepareStatements() {
        try {
            insertUser = connection.prepareStatement(
                "INSERT INTO RestCalculator.Users(username, password, enabled) " +
                    "VALUES (?, ?, TRUE)");

            insertVariable = connection.prepareStatement(
                "INSERT INTO RestCalculator.Variables(id, name, value) " +
                    "VALUES (?, ?, ?)");
            insertFunction = connection.prepareStatement(
                "INSERT INTO RestCalculator.Functions(id, name, body, params) " +
                    "VALUES (?, ?, ?, ?)");

            deleteVariable = connection.prepareStatement(
                "DELETE FROM RestCalculator.Variables WHERE ID = ? AND NAME = ?");

            deleteFunction = connection.prepareStatement(
                "DELETE FROM RestCalculator.Functions WHERE ID = ? AND NAME = ?");

        } catch (SQLException ex) {
            LOG.debug(ex.getMessage());
        }
    }

    public void initSchema() {
        LOG.trace("Initializing schema");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS RestCalculator");
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS RestCalculator.Users (" +
                "id INTEGER NOT NULL AUTO_INCREMENT," +
                "username VARCHAR NOT NULL," +
                "password VARCHAR," +
                "enabled BOOLEAN," +
                "PRIMARY KEY (username)" +
                ");"
        );

        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS RestCalculator.Functions (" +
                "id INTEGER NOT NULL," +
                "name VARCHAR NOT NULL," +
                "body VARCHAR," +
                "params VARCHAR," +
                "PRIMARY KEY (id, name)" +
                ");"
        );

        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS RestCalculator.Variables (" +
                "id INTEGER NOT NULL," +
                "name VARCHAR NOT NULL," +
                "value VARCHAR," +
                "PRIMARY KEY (id, name)" +
                ");"
        );

        try {
            jdbcTemplate.update("INSERT INTO RestCalculator.Users(username, password, enabled) " +
                "VALUES ('admin', 'admin', TRUE)");
        } catch (Exception ex) {
            LOG.debug(ex.getMessage());
        }
    }

    public RestUser loadUser(String username) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + username);
        return jdbcTemplate.queryForObject(
            "SELECT id, username, password, enabled FROM RestCalculator.users WHERE username = ?",
            new Object[]{username},
            (rs, rowNum) ->
                new RestUser(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getBoolean("enabled")
                )
        );
    }

    public Function loadFunction(Integer userId, String functionName) throws EmptyResultDataAccessException {
        return jdbcTemplate.queryForObject(
            "SELECT body, params FROM RestCalculator.Functions WHERE id = ? AND name = ?",
            new Object[]{userId, functionName},
            (rs, rowNb) -> {
                String paramsStr = rs.getString("params");
                List<String> params = Arrays.stream(paramsStr.split("&")).collect(Collectors.toList());
                Function result;
                try {
                    result = new Function(rs.getString("body"), params, null, null);
                } catch (ParsingException e) {
                    result = null;
                }
                return result;
            }
        );
    }

    public Function loadVariable(Integer userId, String variableName) throws EmptyResultDataAccessException {
        return jdbcTemplate.queryForObject(
            "SELECT value FROM RestCalculator.Variables WHERE id = ? AND name = ?",
            new Object[]{userId, variableName},
            (rs, rowNb) -> {
                Function result;
                try {
                    result = new Function(rs.getString("value"), null, null, null);
                } catch (ParsingException e) {
                    result = null;
                }
                return result;
            }
        );
    }

    public List<String> loadFunctions(Integer userId) throws EmptyResultDataAccessException {
        List<Map<String, Object>> temp = jdbcTemplate.queryForList(
            "SELECT name FROM RestCalculator.Functions WHERE ID = ?", userId);
        List<String> result = new ArrayList<>();
        for (Map<String, Object> mp : temp) {
            result.add((String) mp.get("name"));
        }
        return result;
    }

    public List<String> loadVariables(Integer userId) {
        List<Map<String, Object>> temp = jdbcTemplate.queryForList(
            "SELECT name FROM RestCalculator.Variables WHERE ID = ?", userId);
        List<String> result = new ArrayList<>();
        for (Map<String, Object> mp : temp) {
            result.add((String) mp.get("name"));
        }
        return result;
    }


    public void addUser(String username, String password) {
        try {
            insertUser.setString(1, username);
            insertUser.setString(2, password);
            insertUser.execute();
        } catch (SQLException e) {
            LOG.debug(e.getMessage());
        }
    }

    public void addVariable(Integer userId, String variableName, String variableBody) {
        try {
            insertVariable.setInt(1, userId);
            insertVariable.setString(2, variableName);
            insertVariable.setString(3, variableBody);
            insertVariable.execute();
        } catch (SQLException e) {
            LOG.debug(e.getMessage());
        }
    }

    public void addFunction(Integer userId, String functionName, List<String> functionParams, String functionBody) {
        try {
            insertFunction.setInt(1, userId);
            insertFunction.setString(2, functionName);
            insertFunction.setString(3, functionBody);
            StringBuilder params = new StringBuilder();
            for (int i = 0; i < functionParams.size(); ++i) {
                if (i != 0) {
                    params.append("&");
                }
                params.append(functionParams.get(i));
            }
            insertFunction.setString(4, params.toString());
            insertFunction.execute();
        } catch (SQLException e) {
            LOG.debug(e.getMessage());
        }
    }

    public void deleteVariable(Integer userId, String variableName) {
        try {
            deleteVariable.setInt(1, userId);
            deleteVariable.setString(2, variableName);
            deleteVariable.execute();
        } catch (SQLException e) {
            LOG.debug(e.getMessage());
        }
    }

    public void deleteFunction(Integer userId, String functionName) {
        try {
            deleteFunction.setInt(1, userId);
            deleteFunction.setString(2, functionName);
            deleteFunction.execute();
        } catch (SQLException e) {
            LOG.debug(e.getMessage());
        }
    }

}