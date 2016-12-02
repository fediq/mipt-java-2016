package ru.mipt.java2016.homework.g595.novikov.task4;

import java.util.Collection;
import java.util.List;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by igor on 11/28/16.
 */
interface CalculatorWithMethods extends Calculator {
    Double getVariable(String name);

    void addVariable(String name, Double value) throws ParsingException;

    boolean deleteVariable(String name);

    Collection<String> getVariablesList();

    Object getFunction(String name);

    void addFunction(String name, List<String> args, String expr);

    boolean deleteFunction(String name);

    Collection<String> getFunctionsList();

    double calculate(String expr) throws ParsingException;
}
