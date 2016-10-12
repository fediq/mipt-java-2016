package ru.mipt.java2016.homework.g597.dmitrieva.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by macbook on 10.10.16.
 */

public class TestStackCalculator extends AbstractCalculatorTest {
    @Override
    protected  Calculator calc() {
        return new StackCalculator();
    }
}
