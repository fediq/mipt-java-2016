package ru.mipt.java2016.homework.g597.dmitrieva.task4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by irinadmitrieva on 21.12.16.
 */
public class Function {
    public static final Map<String, Integer> PREDEFINED_FUNCTIONS;

    static {
        PREDEFINED_FUNCTIONS = new TreeMap<>();
        PREDEFINED_FUNCTIONS.put("sin", 1);
        PREDEFINED_FUNCTIONS.put("cos", 1);
        PREDEFINED_FUNCTIONS.put("tg", 1);
        PREDEFINED_FUNCTIONS.put("sqrt", 1);
        PREDEFINED_FUNCTIONS.put("pow", 2);
        PREDEFINED_FUNCTIONS.put("abs", 1);
        PREDEFINED_FUNCTIONS.put("sign", 1);
        PREDEFINED_FUNCTIONS.put("log", 1);
        PREDEFINED_FUNCTIONS.put("log2", 1);
        PREDEFINED_FUNCTIONS.put("rnd", 1);
        PREDEFINED_FUNCTIONS.put("max", 2);
        PREDEFINED_FUNCTIONS.put("min", 2);
    }

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

    private Function(String name, int numberOfArguments) {
        this.name = name;
        this.numberOfArguments = numberOfArguments;
        this.arguments = new ArrayList<>();
        for (int i = 0; i < numberOfArguments; i++) {
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