package ru.mipt.java2016.homework.g597.dmitrieva.task4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by irinadmitrieva on 21.12.16.
 */
public class Function {

    private List<String> arguments = new ArrayList<>();
    private String name;
    private String expression;
    private int numberOfArguments;

    public Function(String name, List<String> arguments, String expression) {
        this.name = name;
        this.arguments = arguments;
        this.expression = expression;
        numberOfArguments = arguments.size();
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getExpression() {
        return expression;
    }
}