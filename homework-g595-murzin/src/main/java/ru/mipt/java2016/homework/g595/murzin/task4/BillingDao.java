package ru.mipt.java2016.homework.g595.murzin.task4;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;
import ru.mipt.java2016.homework.g595.murzin.task1.MyContext;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

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
                "(username VARCHAR PRIMARY KEY, password VARCHAR, context VARCHAR)");
//        jdbcTemplate.update("INSERT INTO billing.users VALUES ('dima', 'pass')");
        jdbcTemplate.update("MERGE INTO billing.users (username, password) VALUES('dima', 'pass')");
    }

    public User loadUser(String username) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + username);
        return jdbcTemplate.queryForObject(
                "SELECT username, password FROM billing.users WHERE username = ?",
                new Object[]{username},
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new User(
                                rs.getString("username"),
                                rs.getString("password"),
                                Collections.singletonList(() -> "AUTH")
                        );
                    }
                }
        );
    }

    public void registerUser(String username, String password) {
        jdbcTemplate.update("INSERT INTO billing.users (username, password) VALUES (?, ?)", username, password);
    }

    public MyContext getContext(String username) {
        String context = jdbcTemplate.queryForObject("SELECT context FROM billing.users WHERE username = ?", new Object[]{username}, String.class);
        return new Gson().fromJson(context, MyContext.class);
    }

    public void putContext(String username, MyContext context) {
        String contextJson = new Gson().toJson(context);
        jdbcTemplate.update("MERGE INTO billing.users (username, context) VALUES (?, ?)", username, contextJson);
    }
}
