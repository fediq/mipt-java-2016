package ru.mipt.java2016.homework.g594.pyrkin.task4;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by randan on 12/17/16.
 */
public class BillingVariable {
    private final String name;
    private final double value;
    private final String expression;

    @Autowired

    public BillingVariable(String name, double value, String expression) {
        if (!check(name)) {
            throw new IllegalArgumentException("Invalid variable name");
        }

        this.name = name;
        this.value = value;
        this.expression = expression;
    }

    private boolean check(String stringToName) {
        if (stringToName == null) {
            return false;
        }

        return !(Character.isDigit(stringToName.charAt(0)) || stringToName.contains("[^_a-zA-Z\\d]"));
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
