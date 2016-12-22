package ru.mipt.java2016.homework.g597.dmitrieva.task4;

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
import java.util.*;

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
        //jdbcTemplate.update("INSERT INTO billing.users VALUES ('username', 'password', TRUE)");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.functions" +
                "(username VARCHAR, nameOfFunction VARCHAR, arguments VARCHAR, " +
                "expression VARCHAR, PRIMARY KEY (username, nameOfFunction))");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username','sin', 'a', 'sin(a)')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username','cos', 'a', 'cos(a)')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username','tg', 'a', 'tg(a)')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username','sqrt', 'a', 'sqrt(a)')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username','pow', 'm, e', 'pow(m, e)')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username','abs', 'a', 'abs(a)')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username','sign', 'a', 'sigh(a)')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username','log', 'a, n', 'log(a, n)')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username', 'log2', 'a', 'log2(a)')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username', 'rnd', '', 'rnd()')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username','max', 'a, b', 'max(a, b)')");
        //jdbcTemplate.update("INSERT INTO billing.functions VALUES ('username','min', 'a, b', 'min(a,b)')");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS billing.variables " +
                "(username VARCHAR, nameOfVariable VARCHAR, valueOfVariable DOUBLE NOT NULL, " +
                " PRIMARY KEY (username, nameOfVariable))");

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

    public void addUser(String username, String password) {
        LOG.trace("Adding user " + username);
        jdbcTemplate.update("INSERT INTO billing.users VALUES ('" + username + "', '" + password + "', TRUE)");
    }

    public Double getVariable(String username, String variableName) {
        return jdbcTemplate.queryForObject("SELECT username, nameOfVariable, valueOfVariable " +
                        "FROM billing.variables WHERE username = ? AND nameOfVariable = ?",
                new Object[]{username, variableName},
                new RowMapper<Double>() {
                    @Override
                    public Double mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        return resultSet.getDouble("valueOfVariable");
                    }
                }
        );
    }

    Map<String, Double> getVariables(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT username, nameOfVariable, valueOfVariable FROM billing.variables WHERE username = ?",
                    new  Object[]{username},
                    new RowMapper<TreeMap<String, Double>>() {
                        @Override
                        public TreeMap<String, Double> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                            TreeMap<String, Double> selection = new TreeMap<String, Double>();
                            while (true) {
                                selection.put(resultSet.getString("nameOfVariable"),
                                        resultSet.getDouble("valueOfVariable"));
                                if (!resultSet.next()) {
                                    break;
                                }
                            }
                            return selection;
                        }
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return new TreeMap<>();
        }
    }

    boolean deleteVariable(String username, String nameOfVariable) {
        try {
            getVariable(username, nameOfVariable);
            jdbcTemplate.update("DELETE FROM billing.variables WHERE username = ? AND nameOfVariable = ?",
                    new Object[]{username, nameOfVariable});
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public void addVariable(String username, String nameOfVariable, Double valueOfVariable) throws ParsingException {
        try {
            getVariable(username, nameOfVariable);
            jdbcTemplate.update("DELETE FROM billing.variables WHERE username = ? AND  nameOfVariable = ?",
                    new Object[]{username, nameOfVariable});
            jdbcTemplate.update("INSERT INTO billing.variables VALUES (?, ?, ?)",
                    new Object[]{username, nameOfVariable, valueOfVariable});
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO billing.variables VALUES (?, ?, ?)",
                    new Object[]{username, nameOfVariable, valueOfVariable});
        }
    }

    public Function getFunction(String username, String nameOfFunction) {
        return jdbcTemplate.queryForObject(
                "SELECT username, nameOfFunction, arguments, expression " +
                        "FROM billing.functions WHERE username = ? AND nameOfFunction = ?",
                new Object[]{username, nameOfFunction},
                new RowMapper<Function>() {
                    @Override
                    public Function mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                        String nameOfFunction = resultSet.getString("nameOfFunction");
                        List<String> arguments = Arrays.asList(resultSet.getString("arguments").split(" "));
                        String expression = resultSet.getString("expression");
                        return new Function(nameOfFunction, arguments, expression);
                    }
                }
        );
    }


    TreeMap<String, Function> getFunctions(String username) {
        try {
            return jdbcTemplate.queryForObject("SELECT username, nameOfFunction, arguments, expression" +
                            " FROM billing.functions WHERE username = ?",
                    new Object[]{username},
                    new RowMapper<TreeMap<String, Function>>() {
                        @Override
                        public TreeMap<String, Function> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                            TreeMap<String, Function> selection = new TreeMap();
                            while (true) {
                                String nameOfFunction = resultSet.getString("nameOfFunction");
                                List<String> arguments = Arrays.asList(resultSet.getString("arguments").split(" "));
                                String expression = resultSet.getString("expression");
                                selection.put(nameOfFunction, new Function(nameOfFunction, arguments, expression));
                                if (!resultSet.next()) {
                                    break;
                                }
                            }
                            return selection;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            return new TreeMap<>();
        }
    }

    boolean deleteFunction(String username, String nameOfFunction) {
        try {
            getFunction(username, nameOfFunction);
            jdbcTemplate.update("DELETE FROM billing.functions WHERE username = ? AND  nameOfFunction = ?",
                    new Object[]{username, nameOfFunction});
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    void addFunction(String username, String nameOfFunction, List<String> arguments, String expression) {
        // Заменяем в функции все вхождения переменных на их значения
        int beginIndexOfVariable = 0;
        int endIndexOfVariable = 0;
        boolean isReadingVariable = false;
        for (int i = 0; i < expression.length(); i++) {
            // Нашли что-то, что начинается с буквы -- возможно, это переменная
            if (Character.isLetter(expression.charAt(i)) && !isReadingVariable) {
                beginIndexOfVariable = i;
                endIndexOfVariable = i; // ???
                isReadingVariable = true;
                continue;
            }
            // находимся в процессе чтения переменной (если это она)
            if ((Character.isLetterOrDigit(expression.charAt(i)) || expression.charAt(i) == '_') && isReadingVariable) {
                endIndexOfVariable = i;
                continue;
            }
            if (!(Character.isLetterOrDigit(expression.charAt(i)) || expression.charAt(i) == '_')
                    && isReadingVariable) {
                isReadingVariable = false;
                String variable = expression.substring(beginIndexOfVariable, endIndexOfVariable + 1);
                // Если мы нашли не переменную, а какую-то функцию, то ничего с ней делать не хотим
                if (getFunctions(username).containsKey(variable)) {
                    continue;
                }
                // Если какую-то переменную мы нашли, но в списке переменных это пользователя ее нет,
                // значит, это просто аргумент функции и можно расслабиться
                if (!getVariables(username).containsKey(variable)) {
                    continue;
                }
                // Если же это действительно переменная, добавленная пользователем,
                // то получаем ее значение
                String value = getVariable(username, variable).toString();
                // Заменяем ее первое вхождение на значение
                expression.replaceFirst(variable, value);
                // Дальше обновляем счетчик и снова ищем какую-нибудь переменную
                i = 0;
            }
        }
        try {
            getFunction(username, nameOfFunction);
            jdbcTemplate.update("DELETE FROM billing.functions WHERE username = ? AND nameOfFunction = ?",
                    new Object[]{username, nameOfFunction});
            String stringOfArguments = String.join(" ", arguments);
            jdbcTemplate.update("INSERT INTO billing.functions VALUES (?, ?, ?, ?)",
                    new Object[]{username, nameOfFunction, stringOfArguments, expression});
        } catch (EmptyResultDataAccessException e) {
            String stringArguments = String.join(" ", arguments);
            jdbcTemplate.update("INSERT INTO billing.functions VALUES (?, ?, ?, ?)",
                    new Object[]{username, nameOfFunction, stringArguments, expression});
        }
    }
}
