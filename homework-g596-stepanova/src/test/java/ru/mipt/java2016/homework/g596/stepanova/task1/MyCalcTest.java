package ru.mipt.java2016.homework.g596.stepanova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.tests.task1.AbstractCalculatorTest;

public class MyCalcTest extends AbstractCalculatorTest {
    @Override
    protected Calculator calc() {
        return new MyCalc();
    }
}
