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
        if (!findUser("username"))
        {
            addUserToDb("username", "password");
        }
    }

    private void addUserToDb(String name, String pass) {
        jdbcTemplate.update("INSERT INTO billing.users VALUES (?, ?, TRUE)", name, pass);
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + name);
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + name + ".variables" +
                "(variable VARCHAR PRIMARY KEY, value DOUBLE, expression VARCHAR)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + name + ".functions" +
                "(function VARCHAR PRIMARY KEY, num INT, args VARCHAR, expression VARCHAR, real VARCHAR)");
    }

    public boolean findUser(String username) {
        LOG.debug("Finding user " + username);
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT username FROM billing.users WHERE username = ?",
                    new Object[]{username},
                    new RowMapper<Boolean>() {
                        @Override
                        public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return true;
                        }
                    }
            );
        } catch (EmptyResultDataAccessException exp) {
            return false;
        }
    }

    public void setUser(String name, String pass)
    {
        LOG.debug("Setting user " + name);
        if (findUser(name)) {
            LOG.trace("Occupied user " + name);
            throw new IllegalStateException("Already in database");
        } else {
            addUserToDb(name, pass);
        }
        LOG.trace("Set user " + name);
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

    public boolean findVariable(String name) {
        LOG.trace("Finding variable " + name);
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT variable FROM " + curUser.getUsername() + ".variables WHERE variable = ?",
                    new Object[]{name},
                    new RowMapper<Boolean>() {
                        @Override
                        public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return true;
                        }
                    }
            );
        } catch (EmptyResultDataAccessException exp) {
            return false;
        }
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
                "SELECT variable, value, expression FROM "+ curUser.getUsername() + ".variables WHERE variable = ?",
                new Object[]{variableName},
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("expression") + " current value: " + rs.getDouble("value");
                    }
                }
        );
    }

    public void putVariableValue(String variableName, double value, String expression) {
        LOG.trace("Request put variable value " + variableName + " to database");
        if(findFunction(variableName))
        {
            throw new IllegalStateException("Another type");
        }
        else
        {
            if (findVariable(variableName))
            {
                jdbcTemplate.update("UPDATE " + curUser.getUsername() + ".variables SET value = " + value + ", expression = '" +expression+"' WHERE variable = '" + variableName +"'");
            }
            else
            {
                jdbcTemplate.update("INSERT INTO " + curUser.getUsername() + ".variables VALUES (?, ?, ?)", variableName, value, expression);
            }
        }
    }

    public boolean findFunction(String name) {
        LOG.trace("Finding function " + name);
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT function FROM " + curUser.getUsername() + ".functions WHERE function = ?",
                    new Object[]{name},
                    new RowMapper<Boolean>() {
                        @Override
                        public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return true;
                        }
                    }
            );
        } catch (EmptyResultDataAccessException exp) {
            return false;
        }
    }

    public void putFunctionValue(String functionName, int numargs, String vars, String expression, String real) {
        LOG.trace("Request put function value " + functionName + " to database");
        if (findVariable(functionName)) {
            throw new IllegalStateException("Another type");
        } else {
            if (findFunction(functionName)) {
                jdbcTemplate.update("UPDATE " + curUser.getUsername() + ".functions SET "
                        + "num = " + numargs + ", args = '" + vars + "', expression = '" + expression +
                        "', real = '" + real + "' WHERE function = '" + functionName +"'");
            } else {
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
        LOG.trace("Deleting " + variableName);
        jdbcTemplate.update("DELETE FROM " + curUser.getUsername() + ".variables WHERE variable = '" + variableName + "'");
    }

    public void delFunction(String functionName) {
        LOG.trace("Deleting " + functionName);
        jdbcTemplate.update("DELETE FROM " + curUser.getUsername() + ".functions WHERE function = '" + functionName + "'");
    }

    public List<String> loadAllVariables() {
        LOG.trace("Load all variables");
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
        LOG.trace("Load all functions");
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
