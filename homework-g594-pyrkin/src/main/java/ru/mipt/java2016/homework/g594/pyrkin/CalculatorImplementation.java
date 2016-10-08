package ru.mipt.java2016.homework.g594.pyrkin;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
/**
 * Created by randan on 10/8/16.
 */
public class CalculatorImplementation implements Calculator{
    @Override
    public double calculate(String expression) throws ParsingException{
        if (expression == null)
            throw new ParsingException("Null expression");
        return 0;
    }
}
