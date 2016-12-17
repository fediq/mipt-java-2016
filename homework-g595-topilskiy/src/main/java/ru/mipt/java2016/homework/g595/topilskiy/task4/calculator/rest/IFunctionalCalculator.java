package ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.function.CalculatorFunctionObject;

import java.util.List;

/**
 * Interface for a Calculator which can hold user-defined Functions and Variables
 *
 * @author Artem K. Topilskiy
 * @since  16.12.16.
 */
public interface IFunctionalCalculator {
    /**
     *  Methods of interacting with calculator VARIABLES
     */
    /**
     *  @return the double value under the alias of variableAlias
     */
    Double getVariable(String variableAlias);

    /**
     *  Make the alias of variableAlias reflect to the double value
     */
    boolean putVariable(String variableAlias, Double value);

    /**
     *  Delete the alias of variableAlias and its held value
     */
    boolean deleteVariable(String variableAlias);

    /**
     *  @return the list of aliases of variables in the calculator
     */
    List<String> getVariableList();


    /**
     *  Methods of interacting with calculator FUNCTIONS
     */
    /**
     *  @return a CalculatorFunction object under the alias of functionAlias
     *  NOTE: predefined functions cannot be dealiased
     */
    CalculatorFunctionObject getFunction(String functionAlias);

    /**
     *  Make the alias of functionAlias reflect to CalculatorFunction(expression, arguments)
     */
    boolean putFunction(String functionAlias, String expression, List<String> arguments);

    /**
     *  Delete the alias of functionAlias and its held function
     */
    boolean deleteFunction(String functionAlias);

    /**
     *  @return the list of aliases of functions in the calculator
     */
    List<String> getFunctionList();


    /**
     *  Methods of CALCULATION of the value of expression
     *  (using the kept function and variable sets)
     */
    Double calculate(String expression) throws ParsingException;
}
