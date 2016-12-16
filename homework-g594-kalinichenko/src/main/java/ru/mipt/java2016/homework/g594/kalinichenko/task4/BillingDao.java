package ru.mipt.java2016.homework.g594.kalinichenko.task4;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CurrencyEditor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.expression.ParseException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BillingDao {
    private static final Logger LOG = LoggerFactory.getLogger(BillingDao.class);

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private BillingUser curUser;

    @PostConstruct
    public void postConstruct() {
        curUser = new BillingUser("username", "password", true);
        jdbcTemplate = new JdbcTemplate(dataSource, false);
        initSchema();
    }

    public void initSchema() {
        LOG.trace("Initializing schema");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS billing");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.users " +
                "(username VARCHAR PRIMARY KEY, password VARCHAR, enabled BOOLEAN)");
        try
        {
            loadUser("username");
        }
        catch (EmptyResultDataAccessException exp)
        {
            addUserToDb("username", "password");
        }
    }

    public void setUser(String user) throws IllegalStateException, ParsingException
    {
        LOG.trace("Setting user " + user);
        Pair<String, String> pair = parse(user);
        String name = pair.getKey();
        String pass = pair.getValue();
        LOG.trace("Parsed user " + name);
        LOG.trace("Parsed password " + pass);
        try
        {
            loadUser(name);
            LOG.trace("Occupied user " + name);
            throw new IllegalStateException("Already in database");
        }
        catch (EmptyResultDataAccessException exp)
        {
            addUserToDb(name, pass);
        }
        LOG.trace("Set user " + name);
    }

    private void addUserToDb(String name, String pass) {
        jdbcTemplate.update("INSERT INTO billing.users VALUES (?, ?, TRUE)", name, pass);
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + name);
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + name + ".variables" +
                "(variable VARCHAR PRIMARY KEY, value DOUBLE, expression VARCHAR)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + name + ".functions" +
                "(function VARCHAR PRIMARY KEY, num INT, args VARCHAR, expression VARCHAR, real VARCHAR)");
    }

    private Pair<String,String> parse(String user) throws ParsingException {
        StringBuilder username = new StringBuilder();
        StringBuilder userpass = new StringBuilder();
        boolean mode = false;
        for(int i = 0; i < user.length(); ++i) {
            Character c = user.charAt(i);
            if (c.equals(',')) {
                if (!mode && i > 0)
                {
                    mode = true;
                    continue;
                }
                else
                {
                    throw new ParsingException("Invalid username");
                }
            }
            if (mode)
            {
                userpass.append(c);
            }
            else
            {
                username.append(c);
            }
        }
        return new Pair(String.valueOf(username), String.valueOf(userpass));
    }

    public BillingUser loadUser(String username) throws EmptyResultDataAccessException {
        LOG.trace("Querying for user " + username);
        return jdbcTemplate.queryForObject(
                "SELECT username, password, enabled FROM billing.users WHERE username = ?",
                new Object[]{username},
                new RowMapper<BillingUser>() {
                    @Override
                    public BillingUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                        curUser = new BillingUser(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getBoolean("enabled")
                        );
                        return curUser;
                    }
                }
        );
    }

    public Double loadVariableCalculation(String variableName) {
        LOG.trace("Request get variable value " + variableName + " from database");
        return jdbcTemplate.queryForObject(
                "SELECT variable, value FROM "+ curUser.getUsername() + ".variables WHERE variable = ?",
                new Object[]{variableName},
                new RowMapper<Double>() {
                    @Override
                    public Double mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getDouble("value");
                    }
                }
        );
    }

    public String loadVariableExpression(String variableName) {
        LOG.trace("Request get variable expression " + variableName + " from database");
        return jdbcTemplate.queryForObject(
                "SELECT variable, expression FROM "+ curUser.getUsername() + ".variables WHERE variable = ?",
                new Object[]{variableName},
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("expression");
                    }
                }
        );
    }

    public void putVariableValue(String variableName, double value, String expression) {
        LOG.trace("Request put variable value " + variableName + " to database");
        /*jdbcTemplate.execute("DROP TABLE " + curUser.getUsername() + ".variables");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + curUser.getUsername() + ".variables" +
                "(variable VARCHAR PRIMARY KEY, value DOUBLE, expression VARCHAR)");*/
        try
        {
            loadFunctionExpression(variableName);
            throw new IllegalStateException("Another type");
        }
        catch (EmptyResultDataAccessException ex)
        {
            try
            {
                loadVariableCalculation(variableName);
                jdbcTemplate.update("UPDATE " + curUser.getUsername() + ".variables SET value = " + value + ", expression = '" +expression+"' WHERE variable = '" + variableName +"'");
            }
            catch (EmptyResultDataAccessException exp)
            {
                jdbcTemplate.update("INSERT INTO " + curUser.getUsername() + ".variables VALUES (?, ?, ?)", variableName, value, expression);
            }
        }
    }

    public void putFunctionValue(String functionName, int numargs, String vars, String expression, String real) {
        LOG.trace("Request put function value " + functionName + " to database");
        /*jdbcTemplate.execute("DROP TABLE " + curUser.getUsername() + ".functions");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + curUser.getUsername() + ".functions" +
                "(function VARCHAR PRIMARY KEY, num INT, args VARCHAR, expression VARCHAR, real VARCHAR)");
        */
        try
        {
            loadVariableCalculation(functionName);
            throw new IllegalStateException("Another type");
        }
        catch (EmptyResultDataAccessException ex)
        {
            try
            {
                loadFunctionExpression(functionName);
                jdbcTemplate.update("UPDATE " + curUser.getUsername() + ".functions SET "
                        + "num = " + numargs + ", args = '" + vars + "', expression = '" + expression +
                        "', real = '" + real + "' WHERE function = '" + functionName +"'");
            }
            catch (EmptyResultDataAccessException exp)
            {
                jdbcTemplate.update("INSERT INTO " + curUser.getUsername() + ".functions VALUES (?, ?, ?, ?, ?)", functionName, numargs, vars, expression, real);
            }
        }
    }

    public String loadFunctionExpression(String functionName) {
        LOG.trace("Request get function value " + functionName + "from database");
        return jdbcTemplate.queryForObject(
                "SELECT function, args, expression FROM "+ curUser.getUsername() + ".functions WHERE function = ?",
                new Object[]{functionName},
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return "Args: " + rs.getString("args") + " Value: " + rs.getString("expression");
                    }
                }
        );
    }

    public Pair<String, Integer> loadFunctionCalculation(String functionName) {
        LOG.trace("Request get function to calculate " + functionName + "from database");
        return jdbcTemplate.queryForObject(
                "SELECT function, num, real FROM "+ curUser.getUsername() + ".functions WHERE function = ?",
                new Object[]{functionName},
                new RowMapper<Pair<String, Integer>>() {
                    @Override
                    public Pair<String, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Pair(rs.getString("real"), rs.getInt("num"));
                    }
                }
        );
    }


    public void delVariable(String variableName) {
        System.out.println(variableName);
        System.out.println(loadVariableCalculation(variableName));
        jdbcTemplate.update("DELETE FROM " + curUser.getUsername() + ".variables WHERE variable = '" + variableName + "'");
    }
    public void delFunction(String functionName) {
        System.out.println(functionName);
        System.out.println(loadFunctionCalculation(functionName));
        jdbcTemplate.update("DELETE FROM " + curUser.getUsername() + ".functions WHERE function = '" + functionName + "'");
    }

    public List<String> loadAllVariables() {
        List<String> res = new ArrayList<>();
        return jdbcTemplate.query(
                "SELECT variable FROM "+ curUser.getUsername() + ".variables",
                new Object[] {},
                new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("variable");
                    }
                }
        );
    }

    public List<String> loadAllFunctions() {
        List<String> res = new ArrayList<>();
        return jdbcTemplate.query(
                "SELECT function FROM "+ curUser.getUsername() + ".functions",
                new Object[] {},
                new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("function");
                    }
                }
        );
    }
}
