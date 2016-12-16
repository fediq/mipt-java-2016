package ru.mipt.java2016.homework.g595.topilskiy.task4.calculator.rest.function;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;

/**
 * Interface for a Function which can be evaluated on a set of set arguments
 *
 * @author Artem K. Topilskiy
 * @since  16.12.16.
 */
public interface IEvaluateableFunction {
    /**
     * @return the result of the function evalueated on the given set of arguments
     */
    Double evaluate() throws ParsingException;

    /**
     *  Sets the arguments up for the evaluation of the Function
     */
    void setArguments(List<Double> arguments) throws ParsingException;
}
