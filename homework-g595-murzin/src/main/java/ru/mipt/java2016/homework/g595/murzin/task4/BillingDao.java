package ru.mipt.java2016.homework.g595.murzin.task4;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;
import ru.mipt.java2016.homework.g595.murzin.task1.MyContext;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Collections;
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
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS billing");
        jdbcTemplate.execute("CREATE SCHEMA billing");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.users " +
                "(username VARCHAR PRIMARY KEY, password VARCHAR, context VARCHAR)");
        jdbcTemplate.update("MERGE INTO billing.users (username, password) VALUES('user', '')");
    }

    public User loadUser(String username) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + username);
        return jdbcTemplate.queryForObject(
                "SELECT username, password FROM billing.users WHERE username = ?",
                new Object[]{username},
                (rs, rowNum) -> new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        Collections.singletonList(() -> "AUTH")
                )
        );
    }

    public void registerUser(String username, String password) {
        jdbcTemplate.update("INSERT INTO billing.users (username, password) VALUES (?, ?)", username, password);
    }

    public MyContext getContext(String username) {
        String contextJson = jdbcTemplate.queryForObject("SELECT context FROM billing.users WHERE username = ?", new Object[]{username}, String.class);
        MyContext context = new Gson().fromJson(contextJson, MyContext.class);
        if (context == null) {
            return new MyContext();
        }
        context.resolve();
        return context;
    }

    public void putContext(String username, MyContext context) {
        String contextJson = new Gson().toJson(context);
        jdbcTemplate.update("MERGE INTO billing.users (username, context) VALUES (?, ?)", username, contextJson);
    }

    public String[] getAllUserNames() {
        List<String> strings = jdbcTemplate.queryForList("SELECT username FROM billing.users", String.class);
        return strings.toArray(new String[strings.size()]);
    }

    // for junit
    public void clearContext() {
        putContext(SecurityContextHolder.getContext().getAuthentication().getName(), null);
    }

    public void clearAll() {
        jdbcTemplate.execute("TRUNCATE TABLE billing.users");
    }
}
