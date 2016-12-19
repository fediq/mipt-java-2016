package ru.mipt.java2016.homework.g596.kupriyanov.task4;

/**
 * Created by Artem Kupriyanov on 17/12/2016.
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

        // ЗДЕСЬ ДОЛЖНА БЫТЬ ХОРОШАЯ РЕАЛИЗАЦИЯ БАЗЫ ДАННЫХ

        // Users table
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.users " +
                "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        jdbcTemplate.update("INSERT INTO billing.users VALUES ('username', 'password', TRUE)");

        // Variable table
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.variables " +
                "(username VARCHAR, variable VARCHAR, value FLOAT) " +
                "FOREIGN KEY (username) REFERENCES billing.users(username)");

        // Functions table
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.functions " +
                "(username VARCHAR, function VARCHAR, arity INTEGER, body VARCHAR) " +
                "FOREIGN KEY (username) REFERENCES billing.users(username)");
    }

    // ЗДЕСЬ ДОЛЖНА БЫТЬ ХОРОШАЯ РЕАЛИЗАЦИЯ БАЗЫ ДАННЫХ

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

    public void putUser(String username, String password) {
        jdbcTemplate.update("case (select count(*) from billing.users) = 0 " +
                "where username = '" + username + "' and password = '" + password + "' " +
                "BEGIN " +
                "INSERT INTO billing.users VALUES ('" + username + "', '" + password + "', TRUE) " +
                "END");
    }

    public Double loadVariable(String username, String variable) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + username + " and his variable " + variable);
        return jdbcTemplate.queryForObject(
                "select value from billing.variables " +
                        "where username = '" + username + "' and variable = '" + variable + "'",
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

    public void putVariable(String username, String variable, Double value) {
        LOG.trace("Putting variable " + variable + " for user " + username);
        jdbcTemplate.execute("INSERT INTO billing.variables VALUES ('"
                + username + "', '" + variable + "', " + value.toString() + ")");
    }
}