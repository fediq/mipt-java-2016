package ru.mipt.java2016.homework.g595.novikov.task4;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.novikov.task1.MyCalculator;

/**
 * Created by igor on 11/27/16.
 */
class CalculatorState implements CalculatorWithMethods {
    private MyCalculator calculator = new MyCalculator();
    private Map<String, Double> variables = new HashMap<>();
    private Map<String, MyCalculator.MyFunction> functions = new HashMap<>();

    // TODO : many validations

    @Override
    public double getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public void addVariable(String name, Double value) {
        variables.put(name, value);
    }

    @Override
    public void deleteVariable(String name) {
        variables.remove(name);
    }

    @Override
    public Collection<String> getVariablesList() {
        throw new UnsupportedOperationException("getVariablesList"); // TODO : implement this
        // I don't know why this didn't implement
    }

    @Override
    public Object getFunction(String name) {
        return null; // TODO : implement this
        // I don't know what this function must return
    }

    @Override
    public void addFunction(String name, List<String> args, String expr) {
        MyCalculator.MyFunction func = calculator.new MyFunction(args, expr);
        functions.put(name, func);
    }

    @Override
    public void deleteFunction(String name) {
        functions.remove(name);
    }

    @Override
    public Collection<String> getFunctionsList() {
        return null; // TODO : implement this
        // I don't know what this function must return
    }

    @Override
    public double calculate(String expr) throws ParsingException {
        return calculator.calculateExpression(expr, variables, functions);
    }
}
