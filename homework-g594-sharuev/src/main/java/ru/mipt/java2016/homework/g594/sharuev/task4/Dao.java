package ru.mipt.java2016.homework.g594.sharuev.task4;

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
import java.util.Arrays;
import java.util.List;

@Repository
public class Dao {
    private static final Logger LOG = LoggerFactory.getLogger(Dao.class);

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
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS calc");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS calc.users " +
                "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS calc.variables " +
                "(name VARCHAR PRIMARY KEY, value DOUBLE)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS calc.functions " +
                "(name VARCHAR PRIMARY KEY, func VARCHAR, args VARCHAR)");
        // TODO jdbcTemplate.update("INSERT IGNORE INTO calc.users VALUES ('username', 'password', TRUE)");
    }

    public boolean insertVariable(TopCalculatorVariable var) {
        LOG.trace("Querying for variable " + var.getName());
        return jdbcTemplate.update("merge into calc.variables KEY (name, value) values(?,?)",
                var.getName(), var.getValue()) == 1;
    }

    public boolean removeVariable(String varName) {
        LOG.trace("Removing variable " + varName);
        return jdbcTemplate.update("delete from calc.variables where name = ?",
                varName) == 1;
    }

    public TopCalculatorVariable loadVariable(String varName) {
        LOG.trace("Querying for variable " + varName);
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT name, value FROM calc.variables WHERE name = ?",
                    new Object[]{varName},
                    new RowMapper<TopCalculatorVariable>() {
                        @Override
                        public TopCalculatorVariable mapRow(ResultSet rs,
                                                            int rowNum) throws SQLException {
                            return new TopCalculatorVariable(
                                    rs.getString("name"),
                                    Double.parseDouble(rs.getString("value"))
                            );
                        }
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean insertUser(CalculatorUser var) {
        LOG.trace("Querying for user " + var.getUsername());
        return jdbcTemplate.update(
                "merge into calc.users KEY (username, password, enabled) values(?,?, ?)",
                var.getUsername(), var.getPassword(), var.isEnabled()) == 1;
    }

    public boolean removeUser(String username) {
        LOG.trace("Removing user " + username);
        return jdbcTemplate.update("delete from calc.users where username = ?",
                username) == 1;
    }

    public CalculatorUser loadUser(String username) {
        LOG.trace("Querying for variable " + username);
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT name, value FROM calc.variables WHERE name = ?",
                    new Object[]{username},
                    new RowMapper<CalculatorUser>() {
                        @Override
                        public CalculatorUser mapRow(ResultSet rs,
                                                     int rowNum) throws SQLException {
                            return new CalculatorUser(
                                    rs.getString("username"),
                                    rs.getString("password"),
                                    rs.getBoolean("enabled")
                            );
                        }
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean insertFunction(TopCalculatorFunction func) {
        LOG.trace("Querying for variable " + func.getName());
        return jdbcTemplate.update(
                "merge into calc.functions KEY (name, func, args) values(?,?, ?)",
                func.getName(),
                func.getFunc(),
                String.join(",", func.getArgs())
        ) == 1;
    }

    public boolean removeFunction(String name) {
        LOG.trace("Removing function " + name);
        return jdbcTemplate.update("delete from calc.functions where name = ?",
                name) == 1;
    }

    public TopCalculatorFunction loadFunction(String name) {
        LOG.trace("Querying for function " + name);
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT name, func, args FROM calc.functions WHERE name = ?",
                    new Object[]{name},
                    new RowMapper<TopCalculatorFunction>() {
                        @Override
                        public TopCalculatorFunction mapRow(ResultSet rs,
                                                            int rowNum) throws SQLException {
                            return new TopCalculatorFunction(
                                    rs.getString("name"),
                                    rs.getString("func"),
                                    Arrays.asList(rs.getString("args").split(","))
                            );
                        }
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<String> getVariablesNames() {
        LOG.trace("Querying for all variables");
        try {
            return jdbcTemplate.queryForList("SELECT name FROM calc.variables", String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<String> getFunctionsNames() {
        LOG.trace("Querying for all functions");
        try {
            return jdbcTemplate.queryForList("SELECT name FROM calc.functions", String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
