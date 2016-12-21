package ru.mipt.java2016.homework.g596.ivanova.task4;

import java.util.*;

public class Function {
    private List<String> arguments = new ArrayList<>();
    private String name;
    private String expression;

    public Function(String name, List<String> arguments, String expression) {
        this.name = name;
        this.arguments = arguments;
        this.expression = expression;
    }

    private Function(String name, int valency) {
        this.name = name;
        this.arguments = new ArrayList<>();
        for (int i = 0; i < valency; i++) {
            arguments.add("x" + Integer.toString(i));
        }
        this.expression = null;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getExpression() {
        return expression;
    }
}