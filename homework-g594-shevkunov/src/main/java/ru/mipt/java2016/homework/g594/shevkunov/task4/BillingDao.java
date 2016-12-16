package ru.mipt.java2016.homework.g594.shevkunov.task4;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.vars " +
                "(variable VARCHAR PRIMARY KEY, username VARCHAR," +  //TODO FOREIGN KEY
                " value VARCHAR)");

        jdbcTemplate.execute("DELETE FROM billing.users  WHERE username = 'username'");
        jdbcTemplate.update("INSERT INTO billing.users VALUES ('username', 'password', TRUE) ");
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

    public void setVariable(String username, String variable, String value) {
        delVariable(username, variable);
        String querry = "INSERT INTO billing.vars VALUES ('" + variable + "','" + username + "','" + value + "')";
        jdbcTemplate.execute(querry);
    }

    public String getVariable(String username, String variable) {
        LOG.trace("Getting variable " + variable + " for user " + username);
        String querry = "SELECT value FROM billing.vars WHERE username = '" +
                username + "' AND variable = '" + variable + "'";
        return jdbcTemplate.queryForObject(
                querry,
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("value");
                    }
                }
        );
    }

    public List<String> getAllVariables(String username) {
        LOG.trace("Getting all variables for user " + username);
        String querry = "SELECT * FROM billing.vars"; // TODO make faster

        List<String> vars = new ArrayList<String>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(querry);

        for (Map row : rows) {
            vars.add((String)row.get("variable"));
        }
        return vars;
    }

    public void delVariable(String username, String variable) {
        LOG.trace("Deletting variable " + variable + " for user " + username); // TODO check valid
        String querry = "DELETE FROM billing.vars WHERE username = '" +username + "' AND variable = '" + variable +"'";
        jdbcTemplate.execute(querry);
    }
}
