package ru.mipt.java2016.homework.g595.topilskiy.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

/**
 * A simple calculator using only basic features.
 *
 * @author Artem K. Topilskiy
 * @since 10.10.16
 */
public class SimpleCalculator implements Calculator {
    private void check_expression(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
    }

    public double calculate(String expression) throws ParsingException {
        check_expression(expression);

        return 0.0;
    }
}
