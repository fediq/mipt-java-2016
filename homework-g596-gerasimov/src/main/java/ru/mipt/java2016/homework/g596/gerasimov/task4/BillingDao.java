package ru.mipt.java2016.homework.g596.gerasimov.task4;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BillingDao {
    private static final Logger LOG = LoggerFactory.getLogger(BillingDao.class);

    @Autowired private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void postConstruct() {
        jdbcTemplate = new JdbcTemplate(dataSource, false);
        initSchema();
    }

    public void initSchema() {
        LOG.trace("Initializing schema");
//        jdbcTemplate.execute("DROP SCHEMA IF EXISTS billing");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS billing");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.users "
                + "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.variables "
                + "(username VARCHAR, name VARCHAR, value DOUBLE, expression VARCHAR,"
                + " CONSTRAINT PK_variable PRIMARY KEY (username, name))");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.functions "
                + "(username VARCHAR, name VARCHAR, args VARCHAR, expression VARCHAR,"
                + " CONSTRAINT PK_function PRIMARY KEY (username, name))");
        jdbcTemplate.execute("DELETE FROM billing.users WHERE username = 'username'");
        jdbcTemplate.update("INSERT INTO billing.users VALUES ('username', 'password', TRUE)");
    }


    public BillingUser loadUser(String username) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + username);
        return jdbcTemplate.queryForObject(
                "SELECT username, password, enabled FROM billing.users WHERE username = ?",
                new Object[] {username}, new RowMapper<BillingUser>() {
                    @Override
                    public BillingUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new BillingUser(rs.getString("username"), rs.getString("password"),
                                rs.getBoolean("enabled"));
                    }
                });
    }

    public boolean addUser(String username, String password) {
        LOG.trace("Adding user " + username);
        try {
            jdbcTemplate.update("INSERT INTO billing.users VALUES ('" + username + "', '" + password
                    + "', TRUE)");
        } catch (DuplicateKeyException exception) {
            LOG.debug(exception.getMessage());
            return false;
        }
        LOG.trace("User " + username + " was successfully added");
        return true;
    }

    public boolean addVariable(BillingVariable variable) {
        try {
            jdbcTemplate.update("Insert INTO billing.variables VALUES ('" + variable.getUsername()
                    + "', '" + variable.getName() + "', " + variable.getValue() + ", '" + variable
                    .getExpression() + "')");
        } catch (DuplicateKeyException exception) {
            LOG.debug(exception.getMessage());
            return false;
        }
        return true;
    }

    public BillingVariable getVariable(String username, String name) {
        LOG.trace("Querying for variable " + username + ":" + name);
        return jdbcTemplate.queryForObject(
                "SELECT username, name, value, expression FROM billing.variables WHERE "
                        + "username = '" + username + "' AND name = '" + name + "'",
                new RowMapper<BillingVariable>() {
                    @Override
                    public BillingVariable mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new BillingVariable(rs.getString("username"), rs.getString("name"),
                                rs.getDouble("value"), rs.getNString("expression"));
                    }
                });
    }

    public List<BillingVariable> getAllVariables(String username) {
        LOG.trace("Querying for all variables for " + username);
        return jdbcTemplate
                .query("SELECT username, name, value, expression FROM billing.variables WHERE username = '"
                        + username + "'",
                    new RowMapper<BillingVariable>() {
                        @Override
                        public BillingVariable mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return new BillingVariable(rs.getString("username"), rs.getString("name"),
                                    rs.getDouble("value"), rs.getNString("expression"));
                        }
                    });
    }

    public boolean deleteVariable(String username, String name) {
        LOG.trace("Deleting variable " + username + ":" + name);
        try {
            jdbcTemplate.update("DELETE FROM billing.variables WHERE " + "username = '" + username
                    + "' AND name = '" + name + "'");
        } catch (Exception exception) {
            LOG.debug(exception.getMessage());
            return false;
        }
        LOG.trace("Variable " + username + ":" + name + " was successfully deleted");
        return true;
    }

    public boolean deleteAllVariables(String username) {
        LOG.trace("Deleting variables of " + username);
        try {
            jdbcTemplate.update("DELETE FROM billing.variables WHERE " + "username = '" + username
                    + "'");
        } catch (Exception exception) {
            LOG.debug(exception.getMessage());
            return false;
        }
        LOG.trace("Variables of " + username + " were successfully deleted");
        return true;
    }

    public BillingFunction getFunction(String username, String name) {
        LOG.trace("Querying for function " + username + ":" + name);
        return jdbcTemplate.queryForObject(
                "SELECT username, name, args, expression FROM billing.functions WHERE "
                        + "username = '" + username + "' AND name = '" + name + "'",
                new RowMapper<BillingFunction>() {
                    @Override
                    public BillingFunction mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new BillingFunction(rs.getString("username"), rs.getString("name"),
                                decodeArgs(rs.getString("args")), rs.getNString("expression"));
                    }
                });
    }


    public boolean addFunction(BillingFunction function) {
        try {
            jdbcTemplate.update("Insert INTO billing.functions VALUES ('" + function.getUsername()
                    + "', '" + function.getName() + "', '" + encodeArgs(function.getArgsName()) +
                    "', '" + function.getExpression() + "')");
        } catch (DuplicateKeyException exception) {
            LOG.debug(exception.getMessage());
            return false;
        }
        return true;
    }

    public List<BillingFunction> getAllFunctions(String username) {
        LOG.trace("Querying for all functions for " + username);
        return jdbcTemplate
                .query("SELECT username, name, args, expression FROM billing.functions WHERE "
                        + "username = '" + username + "'",
                        new RowMapper<BillingFunction>() {
                            @Override
                            public BillingFunction mapRow(ResultSet rs, int rowNum) throws SQLException {
                                return new BillingFunction(rs.getString("username"), rs.getString("name"),
                                        decodeArgs(rs.getString("args")), rs.getNString("expression"));
                            }
                        });
    }

    public boolean deleteFunction(String username, String name) {
        LOG.trace("Deleting variable " + username + ":" + name);
        try {
            jdbcTemplate.update("DELETE FROM billing.functions WHERE " + "username = '" + username
                    + "' AND name = '" + name + "'");
        } catch (Exception exception) {
            LOG.debug(exception.getMessage());
            return false;
        }
        LOG.trace("Function " + username + ":" + name + " was successfully deleted");
        return true;
    }


    private Vector<String> decodeArgs(String code) {
        List<String> argsName = new Vector<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (char currentChar : code.toCharArray()) {
            if (currentChar == ',') {
                argsName.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            } else {
                stringBuilder.append(currentChar);
            }
        }
        argsName.add(stringBuilder.toString());

        return new Vector(argsName);
    }

    private String encodeArgs(List<String> argsName) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String arg : argsName) {
            stringBuilder.append(arg + ",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }
}