package ru.mipt.java2016.homework.g596.kozlova.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

@Repository
public class BillingDao {

    private static final Logger LOG = LoggerFactory.getLogger(BillingDao.class);

    @Autowired
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void postConstruct() {
        jdbcTemplate = new JdbcTemplate(dataSource, false);
        LOG.trace("Initializing");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS billing");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.users " +
                "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.variables " +
                "(username VARCHAR, name VARCHAR, value DOUBLE, expression VARCHAR)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.functions " +
                "(username VARCHAR, name VARCHAR, arguments VARCHAR, expression VARCHAR)");
        createNewUser("userName", "password", true);
    }

    public boolean createNewUser(String userName, String password, boolean enabled) {
        try {
            loadUser(userName);
            return false;
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO billing.users VALUES (?, ?, ?)", userName, password, enabled);
            return true;
        }
    }

    public BillingUser loadUser(String userName) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + userName);
        return jdbcTemplate.queryForObject(
                "SELECT username, password, enabled FROM billing.users WHERE username = ?",
                new Object[]{userName},
            (ResultSet resultSet, int numberOfRow) -> {
                return new BillingUser (resultSet.getString("userName"), resultSet.getString("password"),
                    resultSet.getBoolean("enabled"));
            }
        );
    }

    public Variable getVariable(String userName, String variable) {
        return jdbcTemplate.queryForObject(
                "SELECT userName, name, value, expression FROM billing.variables WHERE userName = ? AND name = ?",
                new Object[]{userName, variable},
            (ResultSet resultSet, int numberOfRow) -> {
                return new Variable(
                        resultSet.getString("userName"),
                        resultSet.getString("name"),
                        resultSet.getDouble("value"),
                        resultSet.getString("expression"));
                }
        );
    }

    public Map<String, String> getVariables(String userName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT userName, name, value, expression FROM billing.variables WHERE userName = ?",
                    new Object[]{userName},
                (ResultSet resultSet, int numberOfRow) -> {
                    Map<String, String> map = new HashMap<>();
                    while (!resultSet.next()) {
                        map.put(resultSet.getString("name"), Double.toString(resultSet.getDouble("value")));
                    }
                    return map;
                }
            );
        } catch (EmptyResultDataAccessException e) {
            return new HashMap<>();
        }
    }

    public void deleteVariable(String userName, String name) throws ParsingException {
        try {
            getVariable(userName, name);
            jdbcTemplate.update("DELETE FROM billing.variables WHERE userName = ? AND name = ?", userName, name);
        } catch (EmptyResultDataAccessException e) {
            throw new ParsingException("Can't delete");
        }
    }

    public void addVariable(String userName, String name, Double value, String expression) throws ParsingException {
        jdbcTemplate.update("MERGE INTO billing.variables VALUES (?, ?, ?, ?)", userName, name, value, expression);
    }

    public Function getFunction(String userName, String function) {
        return jdbcTemplate.queryForObject(
                "SELECT userName, name, arguments, expression FROM billing.functions WHERE userName = ? AND name = ?",
                new Object[]{userName, function},
            (ResultSet resultSet, int numberOfRow) -> {
                String name = resultSet.getString("name");
                List<String> arguments = Arrays.asList(resultSet.getString("arguments").split(" "));
                String expression = resultSet.getString("expression");
                return new Function(userName, name, arguments, expression);
            }
        );
    }

    public Map<String, Function> getFunctions(String userName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT userName, name, arguments, expression FROM billing.functions WHERE userName = ?",
                    new Object[]{userName},
                (ResultSet resultSet, int numberOfRow) -> {
                    Map<String, Function> map = new HashMap<>();
                    while (true) {
                        String name = resultSet.getString("name");
                        List<String> arguments = Arrays.asList(resultSet.getString("arguments").split(" "));
                        String expression = resultSet.getString("expression");
                        map.put(name, new Function(userName, name, arguments, expression));
                        if (!resultSet.next()) {
                            break;
                        }
                    }
                    return map;
                }
            );
        } catch (EmptyResultDataAccessException e) {
            return new HashMap<>();
        }
    }

    public void deleteFunction(String userName, String name) throws ParsingException {
        try {
            getFunction(userName, name);
            jdbcTemplate.update("DELETE FROM billing.functions WHERE userName = ? AND name = ?", userName, name);
        } catch (EmptyResultDataAccessException e) {
            throw new ParsingException("Can't delete");
        }
    }

    public void addFunction(String userName, String name, List<String> arguments, String expression)
            throws ParsingException {
        jdbcTemplate.update("MERGE INTO billing.functions VALUES (?, ?, ?, ?)", userName, name, arguments, expression);
    }
}