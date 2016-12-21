package ru.mipt.java2016.homework.g597.sigareva.task4;

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
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.users " +
                "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        jdbcTemplate.execute("DELETE FROM billing.users WHERE username = 'username'");
        jdbcTemplate.update("INSERT INTO billing.users VALUES ('username', 'password', TRUE)");
    }

    public BillingUser loadUser(String username) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + username);
        return jdbcTemplate.queryForObject(
                "SELECT username, password, enabled FROM billing.users WHERE username = ?",
                new Object[]{username},
                new RowMapper<BillingUser>() {
                    @Override
                    public BillingUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new BillingUser(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getBoolean("enabled")
                        );
                    }
                }
        );
    }

    public Double getVariable(String username, String variableName) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + username);
        return jdbcTemplate.queryForObject(
                "SELECT username, variableName, value FROM billing.usersVariables WHERE username = '" + username + "' " +
                        "AND variableName = '" + variableName + "'",
                new Object[]{},
                new RowMapper<Double>() {
                    @Override
                    public Double mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Double(
                                rs.getString("value")
                        );
                    }
                }
        );
    }

    public void registerNewUser(String user, String password) {
        jdbcTemplate.update("INSERT INTO billing.users VALUES ('" + user + "', '" + password + "', TRUE)");
    }

    public void addValue(String user, String variableName, String value) {
        System.out.println(user);
        System.out.println(variableName);
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.usersVariables " +
                "(username VARCHAR, variableName VARCHAR PRIMARY KEY, value FLOAT)");
        jdbcTemplate.execute("DELETE FROM billing.usersVariables WHERE variableName = '" + variableName + "' " +
                        "AND username = '" + user + "'");
        jdbcTemplate.update("INSERT INTO billing.usersVariables VALUES ('" + user + "', '" + variableName + "', '" + value + "')");
    }

    public void deleteVariable(String user, String variableName) {
        jdbcTemplate.execute("DELETE FROM billing.usersVariables WHERE variableName = '" + variableName + "' " +
                "AND username = '" + user + "'");
    }
}