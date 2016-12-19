package ru.mipt.java2016.homework.g596.kozlova.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Array;
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
        createNewUser("userName", "password", true);
    }

    public boolean createNewUser(String userName, String password, boolean enabled) {
        try {
            loadUser(userName);
            return false;
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO billing.users VALUES (?, ?, ?)",
                    new Object[]{userName, password, enabled});
            return true;
        }
    }

    public BillingUser loadUser(String userName) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + userName);
        return jdbcTemplate.queryForObject(
                "SELECT username, password, enabled FROM billing.users WHERE username = ?",
                new Object[]{userName},
                new RowMapper<BillingUser>() {
                    @Override
                    public BillingUser mapRow(ResultSet resultSet, int NumberOfRow) throws SQLException {
                        return new BillingUser(
                                resultSet.getString("userName"),
                                resultSet.getString("password"),
                                resultSet.getBoolean("enabled")
                        );
                    }
                }
        );
    }

    public Variable getVariable(String userName, String variable) {
        return jdbcTemplate.queryForObject(
                "SELECT userName, name, value, expression FROM billing.variables WHERE userName = ? AND name = ?",
                new Object[]{userName, variable},
                new RowMapper<Variable>() {
                    @Override
                    public Variable mapRow(ResultSet resultSet, int numberOfRow) throws SQLException {
                        return new Variable(
                                resultSet.getString("userName"),
                                resultSet.getString("name"),
                                resultSet.getDouble("value"),
                                resultSet.getString("expression"));
                    }
                }
        );
    }

    public Map<String, String> getVariables(String userName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT userName, name, value, expression FROM billing.variables WHERE userName = ?",
                    new Object[]{userName},
                    new RowMapper<Map<String, String>>() {
                        @Override
                        public Map<String, String> mapRow(ResultSet resultSet, int numberOfRow) throws SQLException {
                            Map<String, String> map = new HashMap<>();
                            while (!resultSet.next()) {
                                map.put(resultSet.getString("name"), Double.toString(resultSet.getDouble("value")));
                            }
                            return map;
                        }
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return new HashMap<>();
        }
    }

    public void deleteVariable(String userName, String name) throws ParsingException {
        try {
            getVariable(userName, name);
            jdbcTemplate.update("DELETE FROM billing.variables WHERE userName = ? AND name = ?",
                    new Object[]{userName, name});
        } catch (EmptyResultDataAccessException e) {
            throw new ParsingException("Can't delete");
        }
    }

    public void addVariable(String userName, String name, Double value, String expression) throws ParsingException {
        try {
            getVariable(userName, name);
            jdbcTemplate.update("DELETE FROM billing.variables WHERE userName = ? AND name = ?",
                    new Object[]{userName, name});
            jdbcTemplate.update("INSERT INTO billing.variables VALUES (?, ?, ?, ?)",
                    new Object[]{userName, name, value, expression});
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO billing.variables VALUES (?, ?, ?, ?)",
                    new Object[]{userName, name, value, expression});
        }
    }

    public Function getFunction(String userName, String function) {
        return jdbcTemplate.queryForObject(
                "SELECT userName, name, arguments, expression FROM billing.functions WHERE userName = ? AND name = ?",
                new Object[]{userName, function},
                new RowMapper<Function>() {
                    @Override
                    public Function mapRow(ResultSet resultSet, int numberOfRow) throws SQLException {
                        return new Function(
                                resultSet.getString("userName"),
                                resultSet.getString("name"),
                                resultSet.getArray("arguments"),
                                resultSet.getString("expression"));
                    }
                }
        );
    }

    public Map<String, String> getFunctions(String userName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT userName, name, arguments, expression FROM billing.functions WHERE userName = ?",
                    new Object[]{userName},
                    new RowMapper<Map<String, String>>() {
                        @Override
                        public Map<String, String> mapRow(ResultSet resultSet, int numberOfRow) throws SQLException {
                            Map<String, String> map = new HashMap<>();
                            while (!resultSet.next()) {
                                map.put(resultSet.getString("name"), Double.toString(resultSet.getDouble("arguments")));
                            }
                            return map;
                        }
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return new HashMap<>();
        }
    }

    public void deleteFunction(String userName, String name) throws ParsingException {
        try {
            getFunction(userName, name);
            jdbcTemplate.update("DELETE FROM billing.functions WHERE userName = ? AND name = ?",
                    new Object[]{userName, name});
        } catch (EmptyResultDataAccessException e) {
            throw new ParsingException("Can't delete");
        }
    }

    public void addFunction(String userName, String name, Array arguments, String expression) throws ParsingException {
        try {
            getVariable(userName, name);
            jdbcTemplate.update("DELETE FROM billing.functions WHERE userName = ? AND name = ?",
                    new Object[]{userName, name});
            jdbcTemplate.update("INSERT INTO billing.functions arguments (?, ?, ?, ?)",
                    new Object[]{userName, name, arguments, expression});
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO billing.functions FUNCTION (?, ?, ?, ?)",
                    new Object[]{userName, name, arguments, expression});
        }
    }
}