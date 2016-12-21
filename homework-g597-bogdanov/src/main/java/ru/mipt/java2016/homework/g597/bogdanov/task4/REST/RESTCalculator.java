package ru.mipt.java2016.homework.g597.bogdanov.task4.REST;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g597.bogdanov.task4.REST.functions.CalculateableFunction;
import ru.mipt.java2016.homework.g597.bogdanov.task4.REST.functions.CalculatorFunctionObject;
import ru.mipt.java2016.homework.g597.bogdanov.task4.REST.functions.IEvaluateableFunction;
import ru.mipt.java2016.homework.g597.bogdanov.task4.REST.functions.PredefinedFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Semyo_000 on 20.12.2016.
 */
public class RESTCalculator implements IFunctionalCalculator {
    private Map<String, IEvaluateableFunction> functions = new ConcurrentHashMap<>();
    private Map<String, Double> variables = new ConcurrentHashMap<>();

    private void initializePredefinedFunctions() {
        functions.put("sin", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.SIN));
        functions.put("cos", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.COS));
        functions.put("tg", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.TG));
        functions.put("sqrt", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.SQRT));
        functions.put("pow", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.POW));
        functions.put("abs", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.ABS));
        functions.put("sign", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.SIGN));
        functions.put("log", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.LOG));
        functions.put("log2", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.LOG2));
        functions.put("rnd", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.RND));
        functions.put("max", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.MAX));
        functions.put("min", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.MIN));
    }

    public RESTCalculator() {
        initializePredefinedFunctions();
    }

    @Override
    public Double getVariable(String variableAlias) {
        return variables.get(variableAlias);
    }

    @Override
    public boolean putVariable(String variableAlias, Double value) {
        if (functions.containsKey(variableAlias)) {
            return false;
        } else {
            variables.put(variableAlias, value);
            return true;
        }
    }

    @Override
    public boolean deleteVariable(String variableAlias) {
        if (variables.containsKey(variableAlias)) {
            variables.remove(variableAlias);
            return true;
        }

        return false;
    }

    @Override
    public List<String> getVariableList() {
        return variables.keySet().stream().collect(Collectors.toList());
    }

    public CalculatorFunctionObject getFunction(String functionAlias) {
        if (!functions.containsKey(functionAlias) || functions.get(functionAlias).isPredefined()) {
            return null;
        }

        CalculateableFunction function = (CalculateableFunction) functions.get(functionAlias);
        return new CalculatorFunctionObject(function.getFunctionExpression(), function.getParameterList());
    }

    @Override
    public boolean putFunction(String functionAlias, String expression, List<String> arguments) {
        if (variables.containsKey(functionAlias) ||
                (functions.containsKey(functionAlias) && functions.get(functionAlias).isPredefined())) {
            return false;
        }

        try {
            functions.put(functionAlias, new CalculateableFunction(expression, arguments, functions, variables));
        } catch (ParsingException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteFunction(String functionAlias) {
        if (functions.containsKey(functionAlias)) {
            IEvaluateableFunction function = functions.get(functionAlias);
            if (function == null || function.isPredefined()) {
                return false;
            } else {
                functions.remove(functionAlias);
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> getFunctionList() {
        return functions.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public Double calculate(String expression) throws ParsingException {
        return new CalculateableFunction(expression, new ArrayList<>(), functions, variables).evaluate();
    }
}