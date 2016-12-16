package ru.mipt.java2016.homework.g595.topilskiy.task4.server;

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
public class CalculatorDao {
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
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS calculator");
        jdbcTemplate.execute("CREATE TABLE  IF NOT EXISTS calculator.users " +
                             "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        //jdbcTemplate.update("INSERT INTO calculator.users VALUES ('supersanic', 'gottagofast', TRUE)");
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
