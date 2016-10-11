package ru.mipt.java2016.homework.g59x.lavrentyev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * @author Fedor S. Lavrentyev
 * @since 29.09.16
 */
public class FakeCalculator implements Calculator {
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Oops");
        }

        if ("foo".equals(expression)) {
            throw new ParsingException("Nooooo");
        }

        double result = Double.parseDouble(expression);
        return result;
    }
}
