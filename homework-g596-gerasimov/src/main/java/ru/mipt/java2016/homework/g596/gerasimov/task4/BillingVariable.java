package ru.mipt.java2016.homework.g596.gerasimov.task4;

/**
 * Created by geras-artem on 19.12.16.
 */
public class BillingVariable {
    private final String username;
    private final String name;
    private final double value;
    private final String expression;

    public BillingVariable(String username, String name, double value, String expression)
            throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Empty variable name");
        }
        this.username = username;
        this.name = name;
        this.value = value;
        this.expression = expression;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public String getExpression() {
        return expression;
    }
}
