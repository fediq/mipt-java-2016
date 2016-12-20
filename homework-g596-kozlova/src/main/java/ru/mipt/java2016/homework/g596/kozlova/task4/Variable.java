package ru.mipt.java2016.homework.g596.kozlova.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Variable {

    private static final Logger LOG = LoggerFactory.getLogger(Variable.class);

    private String userName;
    private String name;
    private double value;
    private String expression;

    public Variable(String u, String n, double v, String e) {
        userName = u;
        name = n;
        value = v;
        expression = e;
    }

    public String getUserName() {
        return userName;
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