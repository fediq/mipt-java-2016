package ru.mipt.java2016.homework.g594.shevkunov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Evaluates a value from expressing
 * Created by shevkunov on 04.10.16.
 */
class PolishCalculator implements Calculator {
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        } else {
            throw new ParsingException("troll");
        }
    }
}
