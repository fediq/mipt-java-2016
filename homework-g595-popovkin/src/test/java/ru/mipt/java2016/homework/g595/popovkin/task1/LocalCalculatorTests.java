package ru.mipt.java2016.homework.g595.popovkin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * @author Andrey A. Popovkin
 * @since 10.10.16
 */
public class LocalCalculatorTests extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyCalculator();
    }
}
