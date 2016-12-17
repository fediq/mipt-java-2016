package ru.mipt.java2016.homework.g594.vishnyakova.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Nina on 16.12.16.
 */

public class Variable {
    private static final Logger LOG = LoggerFactory.getLogger(Variable.class);

    private String username;
    private String name;
    private Double value;
    private String expression;

    public Variable(String user, String nam, Double val, String expr) {
        username = user;
        name = nam;
        value = val;
        expression = expr;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public String getExpression() {
        return expression;
    }
}
