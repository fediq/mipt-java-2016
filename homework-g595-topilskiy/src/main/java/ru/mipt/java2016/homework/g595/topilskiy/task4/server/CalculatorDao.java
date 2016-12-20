package ru.mipt.java2016.homework.g595.topilskiy.task4.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.IFunctionalCalculator;
import ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.RESTCalculator;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@Repository
public class CalculatorDao {
    public static final String ADMIN_USERNAME = "supersanic";
    public static final String ADMIN_PASSWORD = "gottagofast";

    private static final Logger LOG = LoggerFactory.getLogger(CalculatorDao.class);

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
        jdbcTemplate.execute("DROP SCHEMA calculator");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS calculator");
        jdbcTemplate.execute("CREATE TABLE  IF NOT EXISTS calculator.users " +
                             "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        addUserDao(ADMIN_USERNAME, ADMIN_PASSWORD, true);
    }

    public boolean addUserDao(String username, String password, Boolean isEnabled) {
        try {
            loadUser(username);
            LOG.debug("User " + username + " already exists.");
            return false;
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO calculator.users VALUES (?, ?, ?)",
                    new Object[]{username, password, isEnabled});
            LOG.debug("User " + username + " successfully created.");
            return true;
        }
    }

    public HashMap<String, IFunctionalCalculator> getUserCalculators() {
        HashMap<String, IFunctionalCalculator> userCalculators = new HashMap<>();
        userCalculators.put(ADMIN_USERNAME, new RESTCalculator());
        return userCalculators;
    }

    public CalculatorUser loadUser(String username) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + username);
        return jdbcTemplate.queryForObject(
                "SELECT username, password, enabled FROM calculator.users WHERE username = ?",
                new Object[]{username},
                new RowMapper<CalculatorUser>() {
                    @Override
                    public CalculatorUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new CalculatorUser(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getBoolean("enabled")
                        );
                    }
                }
        );
    }
}
