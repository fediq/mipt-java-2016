package ru.mipt.java2016.homework.g595.tkachenko.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

/**
 * Тестировщик калькулятора.
 *
 * by Dmitry Tkachenko, 10.10.2016
 */

public class MyCalcTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyCalc();
    }
}
