package ru.mipt.java2016.homework.g597.vasilyev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by mizabrik on 21.12.16.
 */
public interface ExtendableCalculator extends Calculator {
    boolean supportsFunction(String name);

    double calculate(String expression, Scope scope) throws ParsingException;
}
