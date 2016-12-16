package ru.mipt.java2016.homework.g595.romanenko.task4.calculator;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 26.11.16
 **/

public class RestCalculator implements ICalculator {

    private Map<String, Double> variablesTable = new ConcurrentHashMap<>();
    private Map<String, IEvaluateFunction> functionsTable = new ConcurrentHashMap<>();

    public RestCalculator() {
        addPredefinedFunctions();
    }

    private void addPredefinedFunctions() {
        functionsTable.put("sin", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.SIN));
        functionsTable.put("cos", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.COS));
        functionsTable.put("tg", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.TG));
        functionsTable.put("sqrt", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.SQRT));
        functionsTable.put("pow", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.POW));
        functionsTable.put("abs", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.ABS));
        functionsTable.put("sign", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.SIGN));
        functionsTable.put("log", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.LOG));
        functionsTable.put("log2", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.LOG2));
        functionsTable.put("rnd", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.RND));
        functionsTable.put("max", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.MAX));
        functionsTable.put("min", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.MIN));
    }

    @Override
    public Double getVariable(String variableName) {
        return variablesTable.get(variableName);
    }

    @Override
    public boolean putVariable(String variableName, Double value) {
        if (functionsTable.containsKey(variableName)) {
            return false;
        }
        variablesTable.put(variableName, value);
        return true;
    }

    @Override
    public boolean deleteVariable(String variableName) {
        if (variablesTable.containsKey(variableName)) {
            variablesTable.remove(variableName);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getVariables() {
        return variablesTable.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public CalculatorFunction getFunction(String functionName) {

        if (!functionsTable.containsKey(functionName) || functionsTable.get(functionName).isPredefined()) {
            return null;
        }

        Function function = (Function) functionsTable.get(functionName);
        return new CalculatorFunction(function.getBody(), function.getParams());
    }

    @Override
    public boolean putFunction(String functionName, List<String> args, String functionBody) {
        if (variablesTable.containsKey(functionName) ||
                (functionsTable.containsKey(functionName) && functionsTable.get(functionName).isPredefined())) {
            return false;
        }
        try {
            functionsTable.put(functionName,
                    new Function(
                            functionBody,
                            args,
                            Collections.unmodifiableMap(functionsTable),
                            Collections.unmodifiableMap(variablesTable)));
        } catch (ParsingException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteFunction(String functionName) {
        if (functionsTable.containsKey(functionName)) {
            IEvaluateFunction func = functionsTable.get(functionName);
            if (func == null || func.isPredefined()) {
                return false;
            } else {
                functionsTable.remove(functionName);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getFunctionsNames() {
        return functionsTable.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public Double evaluate(String expression) throws ParsingException {
        return new Function(expression,
                new ArrayList<>(),
                Collections.unmodifiableMap(functionsTable),
                Collections.unmodifiableMap(variablesTable)).evaluate();
    }
}
