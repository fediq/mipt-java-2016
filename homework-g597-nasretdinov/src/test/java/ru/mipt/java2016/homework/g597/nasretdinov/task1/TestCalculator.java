package ru.mipt.java2016.homework.g597.nasretdinov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by Iskander on 13.10.2016.
 */
public class TestCalculator extends AbstractCalculatorTest {

    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }
}