package ru.mipt.java2016.homework.g594.pyrkin.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.mipt.java2016.homework.g594.pyrkin.task4.BillingVariable.BillingVariable;

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
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.users " +
                "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        jdbcTemplate.update("DELETE FROM billing.users WHERE username='username'");
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

    public boolean addUser(String username, String password) {
        try {
            jdbcTemplate.execute("INSERT INTO billing.users VALUES ('" + username + "', '" + password + "', TRUE)");
        } catch (Exception e) {
            return false;
        }

        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + username);
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + username + ".variables" +
                "(name VARCHAR PRIMARY KEY, value DOUBLE, expression VARCHAR)");
        return true;
    }

    public BillingVariable loadVariable(String username, String varname) {
        return jdbcTemplate.queryForObject(
                "SELECT name, value, expression FROM " + username + ".variables WHERE name='" + varname + "'",
                new RowMapper<BillingVariable>() {
                    @Override
                    public BillingVariable mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new BillingVariable(
                                rs.getString("name"),
                                rs.getDouble("value"),
                                rs.getString("expression")
                        );
                    }
                }
        );
    }

    public boolean addVariable(String username, BillingVariable variable) {
        try {
            jdbcTemplate.update("INSERT INTO " + username + ".variables VALUES ('" +
                    variable.getName() + "','" + variable.getValue() + "','" +
                    variable.getExpression() + "')");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean removeVariable(String username, String variableName) {
        try {
            jdbcTemplate.update("DELETE FROM " + username + ".variables WHERE " +
                    "name='" + variableName + "'");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<BillingVariable> loadAllVariables(String username) {
        return jdbcTemplate.query(
                "SELECT name, value, expression FROM " + username + ".variables",
                new RowMapper<BillingVariable>() {
                    @Override
                    public BillingVariable mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new BillingVariable(
                                rs.getString("name"),
                                rs.getDouble("value"),
                                rs.getString("expression")
                        );
                    }
                }
        );
    }
}
