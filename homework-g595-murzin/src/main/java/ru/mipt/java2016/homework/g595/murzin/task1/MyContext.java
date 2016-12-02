package ru.mipt.java2016.homework.g595.murzin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.murzin.task4.MyFunction;
import ru.mipt.java2016.homework.g595.murzin.task4.MyVariable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dima on 02.12.16.
 */
public class MyContext {
    public final HashMap<String, MyVariable> variables = new HashMap<>();
    public final HashMap<String, MyFunction> functions = new HashMap<>();

    public void setVariable(String variableName, String variableExpression) throws ParsingException {
        variables.put(variableName, new MyVariable(variableExpression, new SimpleCalculator().calculate(variableExpression, this, null)));
    }

    public boolean setFunction(String functionName, List<String> arguments, String functionExpresion) {
        if (SimpleCalculator.functions.containsKey(functionName)) {
            return false;
        }
        functions.put(functionName, new MyFunction(functionExpresion, arguments, functionExpresion, this));
        return true;
    }
}
