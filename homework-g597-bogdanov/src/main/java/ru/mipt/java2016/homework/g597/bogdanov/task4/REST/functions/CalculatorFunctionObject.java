package ru.mipt.java2016.homework.g597.bogdanov.task4.REST.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Semyo_000 on 20.12.2016.
 */
public class CalculatorFunctionObject {
    private String expression;
    private List<String> arguments;

    public CalculatorFunctionObject() {
        expression = "";
        arguments = new ArrayList<>();
    }

    public CalculatorFunctionObject(String expression, List<String> arguments) {
        this.expression = expression;
        this.arguments = arguments;
    }

    public String getExpression() {
        return expression;
    }

    public List<String> getArguments() {
        return arguments;
    }


    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public String toString() {
        return "Expression: " + expression + '\n' +
                "Arguments: " + String.join(", ", arguments);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CalculatorFunctionObject)) {
            return false;
        }
        CalculatorFunctionObject comparedCalculatorFunction = (CalculatorFunctionObject) obj;
        return expression.equals(comparedCalculatorFunction.expression) &&
                arguments.equals(comparedCalculatorFunction.arguments);
    }

    public int hashCode() {
        return expression.hashCode() * 31 + arguments.hashCode();
    }
}
