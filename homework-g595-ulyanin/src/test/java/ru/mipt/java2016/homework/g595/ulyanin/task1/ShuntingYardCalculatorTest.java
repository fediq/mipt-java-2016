package ru.mipt.java2016.homework.g595.ulyanin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Created by ulyanin on 11.10.16.
 */
public class ShuntingYardCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new ShuntingYardCalculator();
    }
}
