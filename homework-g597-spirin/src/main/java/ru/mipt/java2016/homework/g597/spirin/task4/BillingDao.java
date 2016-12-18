package ru.mipt.java2016.homework.g597.spirin.task4;

/**
 * Created by whoami on 12/13/16.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BillingDao {
    private static final Logger LOG = LoggerFactory.getLogger(BillingDao.class);

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void postConstruct() {
        jdbcTemplate = new JdbcTemplate(dataSource, false);
        initSchema();
    }

    public void initSchema() {
        LOG.trace("Initializing schema");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS billing");

        // User table
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.users " +
                "(username VARCHAR PRIMARY KEY, password VARCHAR)");

        // Update initial (username, password)
        jdbcTemplate.execute("DELETE FROM billing.users WHERE username = 'username'");
        jdbcTemplate.execute("INSERT INTO billing.users VALUES ('username', 'password')");

        // Variable table
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.variables " +
                "(username VARCHAR, variable VARCHAR, value FLOAT)");

        // Function table
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.functions " +
                "(username VARCHAR, function VARCHAR, arity INTEGER, body VARCHAR)");
    }

    // Load user from user table
    public BillingUser loadUser(String username) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + username);
        return jdbcTemplate.queryForObject(
                "SELECT username, password FROM billing.users WHERE username = ?",
                new Object[]{username},
                new RowMapper<BillingUser>() {
                    @Override
                    public BillingUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new BillingUser(
                                rs.getString("username"),
                                rs.getString("password")
                        );
                    }
                }
        );
    }

    // Put user into user table
    public void putUser(String username, String password) {
        LOG.trace("Putting user (username, password) into table");
        jdbcTemplate.execute("INSERT INTO billing.users VALUES ('"
                + username + "', '" + password + "')");
    }

    // Get value of variable of particular user
    public Double getVariable(String username, String variable) throws EmptyResultDataAccessException {
        LOG.trace("Querying for variable " + variable + " of user " + username);
        return jdbcTemplate.queryForObject(
                "SELECT value FROM billing.variables WHERE username = '" + username +
                        "' AND variable = '" + variable + "'",
                new Double[]{},
                new RowMapper<Double>() {
                    @Override
                    public Double mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Double(
                                rs.getString("value").toString()
                        );
                    }
                }
        );
    }

    // Put variable of particular user
    public void putVariable(String username, String variable, Double value) {
        LOG.trace("Putting variable " + variable + " of user " + username);
        deleteVariable(username, variable);
        jdbcTemplate.execute("INSERT INTO billing.variables VALUES ('"
                + username + "', '" + variable + "', " + value.toString() + ")");
    }

    // Delete variable of particular user
    public void deleteVariable(String username, String variable) {
        LOG.trace("Deleting variable " + variable + " of user " + username);
        jdbcTemplate.execute("DELETE FROM billing.variables WHERE username = '"
                + username + "' AND variable = '" + variable + "'");
    }

    // Get all variables from the service of particular user
    public String[] getAllVariables(String username) {
        LOG.trace("Querying for all variables of user " + username);

        List queryResult = jdbcTemplate.queryForList(
                "SELECT variable FROM billing.variables WHERE username = '" +
                        username + "'");

        String[] variables = new String[queryResult.size()];

        for (int i = 0; i < queryResult.size(); ++i) {
            variables[i] = queryResult.get(i).toString();
        }

        return variables;
    }

    // Get function of particular user
    public String getFunction(String username, String function) {
        LOG.trace("Querying for function " + function + " of user " + username);
        return jdbcTemplate.queryForObject(
                "SELECT body FROM billing.functions WHERE username = '" + username +
                        "' AND function = '" + function + "'",
                new String[]{},
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("body");
                    }
                }
        );
    }

    // Put function of particular user
    public void putFunction(String username, String function, Integer arity, String body) {
        LOG.trace("Putting function " + function + " of user " + username);
        deleteFunction(username, function);
        jdbcTemplate.execute("INSERT INTO billing.functions VALUES ('"
                + username + "', '" + function + "', " + arity.toString() + ", '"
                + body + "')");
    }

    // Delete function of particular user
    public void deleteFunction(String username, String function) {
        LOG.info("Deleting function " + function + " of user " + username);
        jdbcTemplate.execute("DELETE FROM billing.functions WHERE username = '" +
                username + "' AND function = '" + function + "'");
    }
}
