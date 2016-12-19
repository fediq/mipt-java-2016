package ru.mipt.java2016.homework.g597.moiseev.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g597.moiseev.task1.StackCalculator;

import java.util.*;

public class PowerfulCalculator extends StackCalculator {
    public Double calculate(String expression, Map<String, Double> variables,
                            Map<String, Function> functions) throws ParsingException {
        String expressionWithoutVariables = Function.replaceVariables(expression, variables);
        String expressionWithoutFunctions = Function.replaceFunctions(expressionWithoutVariables, functions, this);

        return super.calculate(expressionWithoutFunctions);
    }
}
