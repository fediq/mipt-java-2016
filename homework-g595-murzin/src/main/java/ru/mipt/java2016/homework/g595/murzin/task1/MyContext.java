package ru.mipt.java2016.homework.g595.murzin.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.murzin.task4.MyFunction;
import ru.mipt.java2016.homework.g595.murzin.task4.MyVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dima on 02.12.16.
 */
public class MyContext {
    public final Map<String, MyVariable> variables = new HashMap<>();
    public final Map<String, MyFunction> functions = new HashMap<>();

    public boolean setVariable(String variableName, String variableExpression) throws ParsingException {
        if (SimpleCalculator.FUNCTIONS.containsKey(variableName)) {
            return false;
        }
        double variableValue = new SimpleCalculator().calculate(variableExpression, this, null);
        variables.put(variableName, new MyVariable(variableExpression, variableValue));
        return true;
    }

    public boolean setFunction(String functionName, List<String> arguments, String functionExpresion) {
        if (SimpleCalculator.FUNCTIONS.containsKey(functionName)) {
            return false;
        }
        functions.put(functionName, new MyFunction(arguments, functionExpresion, this));
        return true;
    }

    public void resolve() {
        for (MyFunction function : functions.values()) {
            function.setContext(this);
        }
    }
}
