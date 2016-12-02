package ru.mipt.java2016.homework.g595.murzin.task4;

import java.util.List;

/**
 * Created by dima on 01.12.16.
 */
public class MyFunctionDescription {
    public final String name;
    public final List<String> arguments;
    public final String expression;

    public MyFunctionDescription(String name, List<String> arguments, String expression) {
        this.name = name;
        this.arguments = arguments;
        this.expression = expression;
    }
}
