package ru.mipt.java2016.homework.g597.zakharkin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * @author  izaharkin
 * @since   11.10.16.
 */
public class GrammaticCalculatorTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new GrammaticCalculator();
    }
}