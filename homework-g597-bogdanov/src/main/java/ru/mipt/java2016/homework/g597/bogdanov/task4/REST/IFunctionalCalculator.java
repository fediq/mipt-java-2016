package ru.mipt.java2016.homework.g597.bogdanov.task4.REST;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g597.bogdanov.task4.REST.functions.CalculatorFunctionObject;

import java.util.List;

/**
 * Created by Semyo_000 on 20.12.2016.
 */
public interface IFunctionalCalculator {

    Double getVariable(String variableAlias);

    boolean putVariable(String variableAlias, Double value);

    boolean deleteVariable(String variableAlias);

    List<String> getVariableList();

    CalculatorFunctionObject getFunction(String functionAlias);

    boolean putFunction(String functionAlias, String expression, List<String> arguments);

    boolean deleteFunction(String functionAlias);

    List<String> getFunctionList();

    Double calculate(String expression) throws ParsingException;
}
