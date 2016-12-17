package ru.mipt.java2016.homework.g594.vishnyakova.task4;

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
        initSchema();
    }

    public void initSchema() {
        LOG.trace("Initializing schema");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS billing");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.users " +
                "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.variables " +
                "(username VARCHAR, name VARCHAR, value DOUBLE, expression VARCHAR)");
        addUserIfNotExists("username", "password", true);
    }

    boolean addUserIfNotExists(String username, String password, boolean enabled)
    {
        try {
            loadUser(username);
            return false;
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO billing.users VALUES (?, ?, ?)", new Object[]{username, password, enabled});
            return true;
        }
    }

    Variable getVariable(String username, String variable)
    {
        return jdbcTemplate.queryForObject(
                "SELECT username, name, value, expression FROM billing.variables WHERE username = ? AND name = ?",
                new Object[]{username, variable},
                new RowMapper<Variable>() {
                    @Override
                    public Variable mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Variable(
                                rs.getString("username"),
                                rs.getString("name"),
                                rs.getDouble("value"),
                                rs.getString("expression"));
                    }
                }
        );
    }

    HashMap<String, String> getVariables(String username)
    {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT username, name, value, expression FROM billing.variables WHERE username = ?",
                    new Object[]{username},
                    new RowMapper<HashMap<String, String> >() {
                        @Override
                        public HashMap<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                            HashMap<String, String> tmp = new HashMap<String, String>();
                            while (true)
                            {
                                tmp.put(rs.getString("name"), Double.toString(rs.getDouble("value")));
                                if (!rs.next()) break;
                            }
                            return tmp;
                        }
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            HashMap<String, String> tmp = new HashMap<String, String>();
            return tmp;
        }
    }

    boolean deleteVariable(String username, String name) throws ParsingException {
        try {
            getVariable(username, name);
            jdbcTemplate.update("DELETE FROM billing.variables WHERE username = ? AND name = ?",
                    new Object[]{username, name});
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    void addVariable(String username, String name, Double value, String expression) throws ParsingException {
        try {
            getVariable(username, name);
            jdbcTemplate.update("DELETE FROM billing.variables WHERE username = ? AND name = ?",
                    new Object[]{username, name});
            jdbcTemplate.update("INSERT INTO billing.variables VALUES (?, ?, ?, ?)",
                    new Object[]{username, name, value, expression});
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO billing.variables VALUES (?, ?, ?, ?)",
                    new Object[]{username, name, value, expression});
        }
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
}
