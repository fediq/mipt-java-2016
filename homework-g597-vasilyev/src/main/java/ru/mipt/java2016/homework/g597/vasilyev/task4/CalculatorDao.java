package ru.mipt.java2016.homework.g597.vasilyev.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by mizabrik on 21.12.16.
 */

@Repository
public class CalculatorDao {
    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(CalculatorDao.class);

    @PostConstruct
    public void postConstruct() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        initSchema();
        getFunction("root", "preceq");
    }

    public CalculatorUser loadUser(String username) throws EmptyResultDataAccessException {
        return jdbcTemplate.queryForObject(
                "SELECT username, password FROM calculator.user WHERE username = ?",
                new Object[]{username},
                new RowMapper<CalculatorUser>() {
                    @Override
                    public CalculatorUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new CalculatorUser(rs.getString("username"), rs.getString("password"));
                    }
                }
        );
    }

    public List<UserVariable> getVariables(String username) {
        return jdbcTemplate.query(
                "SELECT name, value FROM calculator.variable WHERE owner = ?",
                new UserVariableMapper(), username);
    }

    public double getVariableValue(String username, String variableName) throws EmptyResultDataAccessException {
        return jdbcTemplate.queryForObject(
                "SELECT value FROM calculator.variable WHERE owner = ? AND name = ?",
                new Object[]{username, variableName},
                Double.class
        );
    }

    public void deleteVariable(String username, String variableName) {
        jdbcTemplate.update("DELETE FROM calculator.variable WHERE owner = ? AND name = ?", username, variableName);
        logger.info("User '{}' deleted variable '{}'.", username, variableName);
    }

    @Transactional
    public boolean setVariableValue(String username, String variableName, Double value) {
        if (countElements("function", username, variableName) == 0) {
            jdbcTemplate.update("MERGE INTO calculator.variable (owner, name, value) VALUES (?, ?, ?)",
                    username, variableName, value);
            logger.info("User '{}' has set variable '{}'.", username, variableName);
            return true;
        } else {
            return false;
        }
    }

    public UserFunction getFunction(String username, String name) {
        return jdbcTemplate.queryForObject(
                "SELECT name, expression, arguments FROM calculator.function WHERE owner = ? AND name = ?",
                new Object[]{username, name}, new UserFunctionMapper());
    }

    public List<UserFunction> getFunctions(String username) {
        return jdbcTemplate.query("SELECT name, expression, arguments FROM calculator.function WHERE owner = ?",
                new UserFunctionMapper(), username);
    }

    public boolean setFunction(String username, UserFunction function) {
        if (countElements("variable", username, function.getName()) == 0) {
            jdbcTemplate.update("MERGE INTO calculator.function (owner, name, expression, arguments)"
                            + " VALUES (?, ?, ?, ?)",
                    username, function.getName(), function.getExpression(), function.getArgs());
            logger.info("User '{}' has set function '{}'.", username, function.getName());
            return true;
        } else {
            return false;
        }
    }

    public void deleteFunction(String username, String name) {
        jdbcTemplate.update("DELETE FROM calculator.function WHERE owner = ? AND name = ?", username, name);
        logger.info("User '{}' has deleted function '{}'.", username, name);
    }

    public CalculatorUser createUser(String username, String password) {
        jdbcTemplate.update("INSERT INTO calculator.user (username, password) VALUES (?, ?)", username, password);
        return new CalculatorUser(username, password);
    }

    private static class UserFunctionMapper implements RowMapper<UserFunction> {
        @Override
        public UserFunction mapRow(ResultSet resultSet, int i) throws SQLException {
            return new UserFunction(resultSet.getString("name"), resultSet.getString("expression"),
                    arrayToString((Object[]) resultSet.getArray("arguments").getArray()));
        }
    }

    private static class UserVariableMapper implements RowMapper<UserVariable> {
        @Override
        public UserVariable mapRow(ResultSet resultSet, int i) throws SQLException {
            return new UserVariable(resultSet.getString("name"), resultSet.getDouble("value"));
        }
    }

    private void initSchema() {
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS calculator");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS calculator.user " +
                "(username VARCHAR PRIMARY KEY, password VARCHAR)");
        jdbcTemplate.update("MERGE INTO calculator.user (username, password) VALUES ('root', 'root')");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS calculator.variable" +
                "(owner VARCHAR, name VARCHAR, value DOUBLE," +
                "PRIMARY KEY (name, owner)," +
                "FOREIGN KEY (owner) REFERENCES calculator.user(username))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS calculator.function" +
                "(owner VARCHAR, name VARCHAR, expression VARCHAR, arguments ARRAY," +
                "PRIMARY KEY (name, owner)," +
                "FOREIGN KEY (owner) REFERENCES calculator.user(username))");
    }

    private static String[] arrayToString(Object[] array) {
        String[] strings = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            strings[i] = (String) array[i];
        }

        return strings;
    }

    private int countElements(String table, String owner, String name) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM calculator." + table
                        + " WHERE owner = ? AND name = ?",
                new Object[]{owner, name}, Integer.class);
    }
}
