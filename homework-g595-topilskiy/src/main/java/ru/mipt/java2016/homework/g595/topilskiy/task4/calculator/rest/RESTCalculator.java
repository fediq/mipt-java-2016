package ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.function.CalculateableFunction;
import ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.function.CalculatorFunctionObject;
import ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.function.IEvaluateableFunction;
import ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.function.PredefinedFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * REST-ready Calculator, which can work with user-defined functions/arguments
 *
 * @author Artem K. Topilskiy
 * @since  16.12.16.
 */
public class RESTCalculator implements IFunctionalCalculator {
    /**
     *  Stored Function and Variable Data
     */
    private Map<String, IEvaluateableFunction> functions = new ConcurrentHashMap<>();
    private Map<String, Double> variables = new ConcurrentHashMap<>();

    /**
     *  Initialization functions
     */
    private void initializePredefinedFunctions() {
        functions.put("sin",  new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.SIN));
        functions.put("cos",  new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.COS));
        functions.put("tg",   new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.TG));
        functions.put("sqrt", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.SQRT));
        functions.put("pow",  new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.POW));
        functions.put("abs",  new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.ABS));
        functions.put("sign", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.SIGN));
        functions.put("log",  new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.LOG));
        functions.put("log2", new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.LOG2));
        functions.put("rnd",  new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.RND));
        functions.put("max",  new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.MAX));
        functions.put("min",  new PredefinedFunction(PredefinedFunction.PredefinedFunctionType.MIN));
    }

    /**
     *  CONSTRUCTOR
     */
    public RESTCalculator() {
        initializePredefinedFunctions();
    }

    /**
     *  OVERRIDE
     */

    /**
     *  Methods of interacting with calculator VARIABLES
     */
    /**
     *  @return the double value under the alias of variableAlias
     */
    @Override
    public Double getVariable(String variableAlias) {
        return variables.get(variableAlias);
    }

    /**
     *  Make the alias of variableAlias reflect to the double value
     */
    @Override
    public boolean putVariable(String variableAlias, Double value) {
        if (functions.containsKey(variableAlias)) {
            return false;
        } else {
            variables.put(variableAlias, value);
            return true;
        }
    }

    /**
     *  Delete the alias of variableAlias and its held value
     */
    @Override
    public boolean deleteVariable(String variableAlias) {
        if (variables.containsKey(variableAlias)) {
            variables.remove(variableAlias);
            return true;
        }

        return false;
    }

    /**
     *  @return the list of aliases of variables in the calculator
     */
    @Override
    public List<String> getVariableList() {
        return variables.keySet().stream().collect(Collectors.toList());
    }


    /**
     *  Methods of interacting with calculator FUNCTIONS
     */
    /**
     *  @return a CalculatorFunction object under the alias of functionAlias
     *  NOTE: predefined functions cannot be dealiased
     */
    @Override
    public CalculatorFunctionObject getFunction(String functionAlias) {
        if (!functions.containsKey(functionAlias) || functions.get(functionAlias).isPredefined()) {
            return null;
        }

        CalculateableFunction function = (CalculateableFunction) functions.get(functionAlias);
        return new CalculatorFunctionObject(function.getFunctionExpression(), function.getParameterList());
    }

    /**
     *  Make the alias of functionAlias reflect to CalculatorFunction(expression, arguments)
     */
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

    /**
     *  Delete the alias of functionAlias and its held function
     */
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

    /**
     *  @return the list of aliases of functions in the calculator
     */
    @Override
    public List<String> getFunctionList() {
        return functions.keySet().stream().collect(Collectors.toList());
    }


    /**
     *  Methods of CALCULATION of the value of expression
     *  (using the kept function and variable sets)
     */
    @Override
    public Double calculate(String expression) throws ParsingException {
        return new CalculateableFunction(expression, new ArrayList<>(), functions, variables).evaluate();
    }
}
