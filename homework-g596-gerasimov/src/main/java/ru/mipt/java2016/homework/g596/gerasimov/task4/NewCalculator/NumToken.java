package ru.mipt.java2016.homework.g596.gerasimov.task4.NewCalculator;

/**
 * Created by geras-artem on 19.12.16.
 */
public class NumToken extends Token {
    private double value;

    public NumToken(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}

