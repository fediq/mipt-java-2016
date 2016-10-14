package ru.mipt.java2016.homework.g597kirilenko.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g597.kirilenko.task1.MyCalculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Natak on 11.10.2016.
 */
public class MyCalculatorTests extends AbstractCalculatorTest{
    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }
}
