package ru.mipt.java2016.homework.g596.stepanova.task4;

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
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class Database {
    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    @Autowired private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void postConstruct() {
        jdbcTemplate = new JdbcTemplate(dataSource, false);
        initSchema();
    }

    public void initSchema() {
        LOG.trace("Initializing schema");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS billing");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.users "
                + "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        jdbcTemplate.update("DELETE FROM billing.users WHERE username = 'admin'");
        jdbcTemplate.update("INSERT INTO billing.users VALUES ('admin', 'admin', TRUE)");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.arguments "
                + "(argument VARCHAR PRIMARY KEY, meaning VARCHAR)");
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

    public void register(String[] tokens) throws ParsingException {
        jdbcTemplate.update("DELETE FROM billing.arguments WHERE argument = '" + tokens[0] + "'");
        jdbcTemplate
                .update("INSERT INTO billing.arguments VALUES('" + tokens[0] + "', '" + tokens[1]
                        + "')");
    }

    public double loadMeaning(String argument) throws IOException {
        return jdbcTemplate.queryForObject(
                "SELECT argument, meaning FROM billing.arguments WHERE argument = ?",
                new Object[] {argument}, new RowMapper<Double>() {
                    @Override
                    public Double mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getDouble("meaning");
                    }
                });
    }

    public void deleteArgument(String argument) throws IOException {
        jdbcTemplate.update("DELETE FROM billing.arguments WHERE argument = '" + argument + "'");
    }

    public double check(String argument) throws IOException {
        int i = jdbcTemplate
                .queryForObject("SELECT 1 COUNT * FROM billing.arguments WHERE argument = ?",
                        new Object[] {argument}, new RowMapper<Integer>() {
                            @Override
                            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                                return resultSet.getInt(1);
                            }
                        });
        if (i > 0) {
            return jdbcTemplate.queryForObject(
                    "SELECT argument, meaning FROM billing.arguments WHERE argument = ?",
                    new Object[] {argument}, new RowMapper<Double>() {
                        @Override
                        public Double mapRow(ResultSet resultSet, int i) throws SQLException {
                            return resultSet.getDouble("meaning");
                        }
                    });
        }
        throw new IOException("Smth wen't wrong");
    }

    public void addUser(String data) {
        String delim = "[;]";
        String[] tokens = data.split(delim);
        jdbcTemplate.update("DELETE FROM billing.users WHERE username = '" + tokens[0] + "'");
        jdbcTemplate.update("INSERT INTO billing.users VALUES('" + tokens[0] + "', '" + tokens[1]
                + "', TRUE)");
    }

    public void deleteUser(String data) {
        jdbcTemplate.update("DELETE FROM billing.users WHERE username = '" + data + "'");
    }

    public boolean checkUser(String data) {
        BillingUser user = loadUser(data);
        return user.getUsername().length() > 0;
    }
}