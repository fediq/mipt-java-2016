package ru.mipt.java2016.homework.g597.vasilyev.task4;

/**
 * Created by mizabrik on 21.12.16.
 */
public class UserVariable {
    private String name;
    private double value;

    public UserVariable(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }
}
