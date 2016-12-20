package ru.mipt.java2016.homework.g596.litvinov.task4;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 19.12.16.
 */
public class BillingVariable {
    private final String name;
    private final double value;
    private final String expression;

    @Autowired
    public BillingVariable(String name, double value, String expression) {
        if (!check(name)) {
            throw new IllegalArgumentException("Invalid name of variable");
        }
        this.name = name;
        this.value = value;
        this.expression = expression;
    }

    private boolean check(String s) {
        if (s == null) {
            return false;
        }
        return !(Character.isDigit(s.charAt(0)) || s.contains("[^_a-zA-Z]"));
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
