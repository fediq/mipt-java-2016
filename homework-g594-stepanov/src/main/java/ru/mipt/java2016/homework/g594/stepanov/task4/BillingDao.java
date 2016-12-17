package ru.mipt.java2016.homework.g594.stepanov.task4;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository
public class BillingDao {
    private static final Logger LOG = LoggerFactory.getLogger(BillingDao.class);

    @Autowired
    private HikariDataSource dataSource;

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
                "(username VARCHAR PRIMARY KEY, password VARCHAR)");
        jdbcTemplate.execute("DELETE FROM billing.users WHERE username = 'username'");
        jdbcTemplate.execute("INSERT INTO billing.users VALUES ('username', 'password')");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.variables " +
                "(username VARCHAR, variable VARCHAR, value FLOAT)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.functions " +
                "(username VARCHAR, function VARCHAR, valency INTEGER, body VARCHAR)");
    }


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

    public void putUser(String username, String password) {
        LOG.info("PUT");
        jdbcTemplate.execute("INSERT INTO billing.users VALUES ('"
                + username + "', '" + password + "')");
    }

    public void deleteVariable(String username, String variable) {
        LOG.info("Delete variable by " + username + " named " + variable);
        jdbcTemplate.execute("DELETE FROM billing.variables WHERE username = '"
                + username + "' AND variable = '" + variable + "'");
    }

    public void putVariable(String username, String variable, Double value) {
        LOG.info("Put variable by " + username + " named " + variable + " with value " + value);
        deleteVariable(username, variable);
        jdbcTemplate.execute("INSERT INTO billing.variables VALUES ('"
                + username + "', '" + variable + "', " + value.toString() + ")");
    }

    public Double getVariable(String username, String variable) throws EmptyResultDataAccessException {
        LOG.info("Get variable by " + username + " named " + variable);
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

    public List<String> getAllVariables(String username) {
        LOG.info("Get all variables by " + username);
        String query = "SELECT variable FROM billing.variables WHERE username = '" +
                username + "'";
        List<String> list = new ArrayList<String>();
        List rows = jdbcTemplate.queryForList(query);
        for (Object row : rows) {
            list.add(row.toString());
        }
        return list;
    }

    public void deleteFunction(String username, String function) {
        LOG.info("Delete function by " + username + " named " + function);
        jdbcTemplate.execute("DELETE FROM billing.functions WHERE username = '" +
                        username + "' AND function = '" + function + "'");
    }

    public void putFunction(String username, String function, Integer valency, String body) {
        LOG.info("Put function by " + username + " named " + function);
        deleteFunction(username, function);
        jdbcTemplate.execute("INSERT INTO billing.functions VALUES ('"
                + username + "', '" + function + "', " + valency.toString() + ", '"
                + body + "')");
    }


    public String getFunction(String username, String function) {
        LOG.info("Get function by " + username + " named " + function);
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

    @PreDestroy
    public void close() {
        dataSource.close();
    }

}
