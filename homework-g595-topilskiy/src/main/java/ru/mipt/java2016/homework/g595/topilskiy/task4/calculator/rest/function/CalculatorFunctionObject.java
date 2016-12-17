package ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.function;

import java.util.ArrayList;
import java.util.List;

/**
 * The Function-Objects held in FunctionalCalculators
 * which hold the argument list and the expression of the function
 *
 * @author Artem K. Topilskiy
 * @since  16.12.16.
 */
public class CalculatorFunctionObject {
    private String expression;
    private List<String> arguments;

    /**
     *  CONSTRUCTORS
     */
    public CalculatorFunctionObject() {
        expression = "";
        arguments = new ArrayList<>();
    }

    public CalculatorFunctionObject(String expression, List<String> arguments) {
        this.expression = expression;
        this.arguments  = arguments;
    }


    /**
     *  GETTERS
     */
    public String getExpression() {
        return expression;
    }

    public List<String> getArguments() {
        return arguments;
    }


    /**
     *  SETTERS
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    /**
     *  OVERRIDDEN
     */
    @Override
    public String toString() {
        return "Expression: " + expression + '\n' +
                "Arguments: " + String.join(", ", arguments);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CalculatorFunctionObject)) {
            return false;
        }
        CalculatorFunctionObject comparedCalculatorFunction = (CalculatorFunctionObject) obj;
        return expression.equals(comparedCalculatorFunction.expression) &&
                arguments.equals(comparedCalculatorFunction.arguments);
    }

    @Override
    public int hashCode() {
        return expression.hashCode() * 31 + arguments.hashCode();
    }
}
