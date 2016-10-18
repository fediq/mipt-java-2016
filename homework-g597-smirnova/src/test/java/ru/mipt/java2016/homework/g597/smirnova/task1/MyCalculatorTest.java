package ru.mipt.java2016.homework.g597.smirnova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Elena Smirnova on 13.10.16.
 */
public class MyCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }

}