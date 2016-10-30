package ru.mipt.java2016.homework.g595.kireev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by Карим on 05.10.2016.
 */
public class WonderCalculator implements Calculator {
    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("troll");
        } else {
            PolishNotation parser = new PolishNotation();
            return parser.calc(expression);
        }
    }
}
